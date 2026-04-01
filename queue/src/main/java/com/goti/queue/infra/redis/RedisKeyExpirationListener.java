package com.goti.queue.infra.redis;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.goti.queue.dto.WaitingQueueLeaveEvent;
import com.goti.queue.repository.WaitingQueueRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

	private final ApplicationEventPublisher eventPublisher;
	private final WaitingQueueRepository waitingQueueRepository;
	private final RedisKeyProvider keyProvider;

	public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer,
		ApplicationEventPublisher eventPublisher,
		com.goti.queue.repository.WaitingQueueRepository waitingQueueRepository, RedisKeyProvider keyProvider) {
		super(listenerContainer);
		// ElastiCache는 CONFIG 명령을 차단하므로 init() 시 CONFIG GET 호출을 skip
		// notify-keyspace-events는 ElastiCache 파라미터 그룹에서 직접 설정
		setKeyspaceNotificationsConfigParameter("");
		this.eventPublisher = eventPublisher;
		this.waitingQueueRepository = waitingQueueRepository;
		this.keyProvider = keyProvider;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();

		if (expiredKey.startsWith(RedisKeyProvider.PREFIX_WAITING_HEARTBEAT) ||
			expiredKey.startsWith(RedisKeyProvider.PREFIX_ACTIVE)) {
			handleExpiration(expiredKey);
		}
	}

	private void handleExpiration(String key) {
		RedisKeyProvider.ExpiredKeyInfo info = keyProvider.parseExpiredKey(key);

		if (info == null) {
			return;
		}

		try {
			UUID gameId = info.gameId();
			UUID userId = info.userId();
			boolean isFromActive = info.isActive();
			Long queueNum = null;

			if (!isFromActive) {
				queueNum = waitingQueueRepository.removeFromWaiting(gameId, userId);
			}

			eventPublisher.publishEvent(new WaitingQueueLeaveEvent(gameId, userId, isFromActive, queueNum));

			log.info("action=LEAVE gameId={} userId={} reason={}", gameId, userId,
				isFromActive ? "ACTIVE_EXPIRED" : "HEARTBEAT_EXPIRED");
		} catch (Exception e) {
			log.error("만료 키 처리 중 오류 발생: {}", key, e);
		}
	}
}
