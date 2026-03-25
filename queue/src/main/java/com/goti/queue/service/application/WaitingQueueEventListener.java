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

			String lockKey = keyProvider.getLockKey(gameId);

			lockManager.withLock(lockKey, () -> {
				if (event.isFromActive()) {
					waitingQueueRepository.incrementCurrentUsers(gameId, -1);
				}

				Long maxCapacityRaw = waitingQueueRepository.getMaxCapacity(gameId);
				long maxCapacity = maxCapacityRaw != null ? maxCapacityRaw : 0L;

				Long currentUsersRaw = waitingQueueRepository.getCurrentUsers(gameId);
				long currentUsers = currentUsersRaw != null ? currentUsersRaw : 0L;

				if (currentUsers < 0) {
					log.error("현재 사용자 수가 음수가 되었습니다. gameId: {}", gameId);
					waitingQueueRepository.incrementCurrentUsers(gameId, (int)Math.abs(currentUsers));
					currentUsers = 0L;
				}

				long availableSlots = maxCapacity - currentUsers;

				if (availableSlots > 0) {
					Long nextAllowedNum = waitingQueueRepository.getNthQueueNum(gameId, availableSlots);
					if (nextAllowedNum != null) {
						waitingQueueRepository.updateAllowedNum(gameId, nextAllowedNum);
					} else {
						Long lastIssued = waitingQueueRepository.getLastIssuedNum(gameId);
						waitingQueueRepository.updateAllowedNum(gameId, lastIssued);
					}
				}

				return null;
			});
		} catch (Exception e) {
			log.error("이탈 이벤트 처리 중 오류 발생. event: {}", event, e);
		}
	}
}
