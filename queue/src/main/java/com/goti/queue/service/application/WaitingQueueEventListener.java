package com.goti.queue.service.application;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.goti.infra.lock.DistributedLockManager;
import com.goti.queue.dto.WaitingQueueLeaveEvent;
import com.goti.queue.infra.config.QueueProperties;
import com.goti.queue.infra.redis.RedisKeyProvider;
import com.goti.queue.repository.WaitingQueueRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingQueueEventListener {

	private final WaitingQueueRepository waitingQueueRepository;
	private final DistributedLockManager lockManager;
	private final RedisKeyProvider keyProvider;
	private final QueueProperties queueProperties;
	private final MeterRegistry meterRegistry;

	@Async
	@EventListener
	public void handleUserLeave(WaitingQueueLeaveEvent event) {
		try {
			UUID gameId = event.gameId();
			UUID userId = event.userId();

			boolean isFirst = waitingQueueRepository.checkDuplicateEventProcess(gameId, userId,
				queueProperties.leaveEventTtl());
			if (!isFirst) {
				log.debug("이미 처리된 이탈 이벤트입니다. gameId: {}, userId: {}", gameId, userId);
				return;
			}

			// 이탈 처리
			meterRegistry.counter("queue.leave.total",
				"gameId", gameId.toString(),
				"reason",
				event.isFromActive() ? "active_expired" : "heartbeat_expired").increment();

			String lockKey = keyProvider.getLockKey(gameId);

			lockManager.withLock(lockKey, () -> {
				if (event.isFromActive()) {
					waitingQueueRepository.updateCurrentUsers(gameId, -1);
				}

				Long maxCapacityRaw = waitingQueueRepository.getMaxCapacity(gameId);
				long maxCapacity = maxCapacityRaw != null ? maxCapacityRaw : 0L;

				Long currentUsersRaw = waitingQueueRepository.getCurrentUsers(gameId);
				long currentUsers = currentUsersRaw != null ? currentUsersRaw : 0L;

				if (currentUsers < 0) {
					log.error("현재 사용자 수가 음수가 되었습니다. gameId: {}", gameId);
					waitingQueueRepository.updateCurrentUsers(gameId, (int)Math.abs(currentUsers));
					currentUsers = 0L;
				}

				long availableSlots = maxCapacity - currentUsers;

				log.info("action=SLOT_RELEASE gameId={} activeCount={} maxCapacity={} availableSlots={}", gameId,
					currentUsers, maxCapacity, availableSlots);

				if (availableSlots > 0) {
					Long nextAllowedNum = waitingQueueRepository.getNthQueueNum(gameId, availableSlots);
					if (nextAllowedNum != null) {
						// 대기 인원이 빈 자리보다 많을 때
						waitingQueueRepository.updateAllowedNum(gameId, nextAllowedNum);
						log.info("action=ADMIT_BATCH gameId={} newAllowedNum={}", gameId, nextAllowedNum);
					} else {
						// 대기 인원이 빈 자리보다 적을 때
						Long lastIssued = waitingQueueRepository.getLastIssuedNum(gameId);
						waitingQueueRepository.updateAllowedNum(gameId, lastIssued);
						log.info("action=ADMIT_BATCH gameId={} newAllowedNum={}", gameId, lastIssued);
					}
				}

				Long waitingSize = waitingQueueRepository.getWaitingSize(gameId);
				meterRegistry.gauge("queue.waiting.size", Tags.of("gameId", gameId.toString()), waitingSize);
				meterRegistry.gauge("queue.max.entry", Tags.of("gameId", gameId.toString()), maxCapacity);
				log.info("action=SLOT_PROCESSED gameId={} activeCount={} waitingCount={} availableSlots={}",
					gameId, currentUsers, waitingSize, availableSlots);
				return null;
			});
		} catch (Exception e) {
			log.error("이탈 이벤트 처리 중 오류 발생. event: {}", event, e);
		}
	}
}
