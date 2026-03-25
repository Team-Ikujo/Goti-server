package com.goti.queue.service.application;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.goti.infra.lock.DistributedLockManager;
import com.goti.queue.dto.WaitingQueueLeaveEvent;
import com.goti.queue.infra.config.QueueProperties;
import com.goti.queue.infra.redis.RedisKeyProvider;
import com.goti.queue.repository.WaitingQueueRepository;

@ExtendWith(MockitoExtension.class)
class WaitingQueueEventListenerTest {

	@Mock
	WaitingQueueRepository waitingQueueRepository;
	@Mock
	DistributedLockManager lockManager;
	@Mock
	RedisKeyProvider keyProvider;
	@Mock
	QueueProperties queueProperties;

	@InjectMocks
	WaitingQueueEventListener waitingQueueEventListener;

	@Test
	void 이탈처리_활성상태_슬롯반환() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		WaitingQueueLeaveEvent event = new WaitingQueueLeaveEvent(gameId, userId, true, 100L);

		given(queueProperties.leaveEventTtl()).willReturn(60L);
		given(waitingQueueRepository.checkDuplicateEventProcess(eq(gameId), eq(userId), anyLong())).willReturn(true);
		given(keyProvider.getLockKey(gameId)).willReturn("lock:key");
		given(waitingQueueRepository.getCurrentUsers(gameId)).willReturn(10L);

		willAnswer(invocation -> {
			Supplier<?> action = invocation.getArgument(1);
			return action.get();
		}).given(lockManager).withLock(anyString(), any(Supplier.class));

		waitingQueueEventListener.handleUserLeave(event);

		verify(waitingQueueRepository).incrementCurrentUsers(gameId, -1);
		verify(waitingQueueRepository).incrementAllowedNum(gameId, 1);
	}

	@Test
	void 이탈처리_대기중_슬롯반환() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		WaitingQueueLeaveEvent event = new WaitingQueueLeaveEvent(gameId, userId, false, 50L);
		String lockKey = "lock:key";

		given(queueProperties.leaveEventTtl()).willReturn(60L);
		given(waitingQueueRepository.checkDuplicateEventProcess(eq(gameId), eq(userId), anyLong())).willReturn(true);
		given(keyProvider.getLockKey(gameId)).willReturn(lockKey);
		given(waitingQueueRepository.getAllowedQueueNum(gameId)).willReturn(100L);

		willAnswer(invocation -> {
			Supplier<?> action = invocation.getArgument(1);
			return action.get();
		}).given(lockManager).withLock(eq(lockKey), any(Supplier.class));

		waitingQueueEventListener.handleUserLeave(event);

		verify(waitingQueueRepository).incrementAllowedNum(eq(gameId), eq(1));
		verify(waitingQueueRepository, never()).incrementCurrentUsers(any(), anyInt());
	}
}
