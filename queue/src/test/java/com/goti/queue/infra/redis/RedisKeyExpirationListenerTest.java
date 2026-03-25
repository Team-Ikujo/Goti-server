package com.goti.queue.infra.redis;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.goti.queue.dto.WaitingQueueLeaveEvent;
import com.goti.queue.repository.WaitingQueueRepository;

@ExtendWith(MockitoExtension.class)
class RedisKeyExpirationListenerTest {

	@Mock
	RedisMessageListenerContainer listenerContainer;
	@Mock
	ApplicationEventPublisher eventPublisher;
	@Mock
	WaitingQueueRepository waitingQueueRepository;
	@Mock
	RedisKeyProvider keyProvider;

	@InjectMocks
	RedisKeyExpirationListener redisKeyExpirationListener;

	@Test
	void 메시지수신_대기열_하트비트_만료() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String expiredKey = RedisKeyProvider.PREFIX_WAITING_HEARTBEAT + gameId + ":" + userId;
		Message message = mock(Message.class);
		given(message.toString()).willReturn(expiredKey);

		given(keyProvider.parseExpiredKey(expiredKey))
			.willReturn(new RedisKeyProvider.ExpiredKeyInfo(gameId, userId, false));
		given(waitingQueueRepository.removeFromWaiting(gameId, userId)).willReturn(100L);

		redisKeyExpirationListener.onMessage(message, null);

		verify(eventPublisher).publishEvent(any(WaitingQueueLeaveEvent.class));
		verify(waitingQueueRepository).removeFromWaiting(gameId, userId);
	}

	@Test
	void 메시지수신_활성세션_만료() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String expiredKey = RedisKeyProvider.PREFIX_ACTIVE + gameId + ":" + userId;
		Message message = mock(Message.class);
		given(message.toString()).willReturn(expiredKey);

		given(keyProvider.parseExpiredKey(expiredKey))
			.willReturn(new RedisKeyProvider.ExpiredKeyInfo(gameId, userId, true));

		redisKeyExpirationListener.onMessage(message, null);

		verify(eventPublisher).publishEvent(any(WaitingQueueLeaveEvent.class));
		verify(waitingQueueRepository, never()).removeFromWaiting(any(), any());
	}
}
