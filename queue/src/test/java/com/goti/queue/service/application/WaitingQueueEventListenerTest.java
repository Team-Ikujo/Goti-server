package com.goti.queue.service.application;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
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
	@DisplayName("활성 사용자가 이탈하면 빈 자리를 계산하여 allowedNum을 다음 대기자 번호로 업데이트한다")
	void 이탈처리_활성상태_슬롯전진() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		WaitingQueueLeaveEvent event = new WaitingQueueLeaveEvent(gameId, userId, true, 100L);

		given(queueProperties.leaveEventTtl()).willReturn(60L);
		given(waitingQueueRepository.checkDuplicateEventProcess(eq(gameId), eq(userId), anyLong())).willReturn(true);
		given(keyProvider.getLockKey(gameId)).willReturn("lock:key");

		willAnswer(invocation -> {
			Supplier<?> action = invocation.getArgument(1);
			return action.get();
		}).given(lockManager).withLock(anyString(), any(Supplier.class));

		given(waitingQueueRepository.getMaxCapacity(gameId)).willReturn(50L);
		given(waitingQueueRepository.getCurrentUsers(gameId)).willReturn(49L); // decrementCurrentUsers(-1) 호출 후의 값으로 가정
		given(waitingQueueRepository.getNthQueueNum(gameId, 1L)).willReturn(150L); // 1번째 대기자 번호가 150번

		// When
		waitingQueueEventListener.handleUserLeave(event);

		// Then
		verify(waitingQueueRepository).updateCurrentUsers(gameId, -1);
		verify(waitingQueueRepository).updateAllowedNum(gameId, 150L);
	}

	@Test
	@DisplayName("대기 중인 사용자가 이탈하더라도 빈 자리가 있으면 allowedNum을 최신화한다")
	void 이탈처리_대기중_슬롯전진() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		WaitingQueueLeaveEvent event = new WaitingQueueLeaveEvent(gameId, userId, false, 200L);
		String lockKey = "lock:key";

		given(queueProperties.leaveEventTtl()).willReturn(60L);
		given(waitingQueueRepository.checkDuplicateEventProcess(eq(gameId), eq(userId), anyLong())).willReturn(true);
		given(keyProvider.getLockKey(gameId)).willReturn(lockKey);

		willAnswer(invocation -> {
			Supplier<?> action = invocation.getArgument(1);
			return action.get();
		}).given(lockManager).withLock(eq(lockKey), any(Supplier.class));

		given(waitingQueueRepository.getMaxCapacity(gameId)).willReturn(50L);
		given(waitingQueueRepository.getCurrentUsers(gameId)).willReturn(40L);
		given(waitingQueueRepository.getNthQueueNum(gameId, 10L)).willReturn(300L); // 10번째 대기자 번호가 300번

		waitingQueueEventListener.handleUserLeave(event);

		verify(waitingQueueRepository, never()).updateCurrentUsers(any(), anyInt());
		verify(waitingQueueRepository).updateAllowedNum(gameId, 300L);
	}

	@Test
	@DisplayName("대기열에 사람이 없으면 마지막 발급 번호까지 allowedNum을 업데이트한다")
	void 이탈처리_대기열부족_마지막번호로업데이트() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		WaitingQueueLeaveEvent event = new WaitingQueueLeaveEvent(gameId, userId, true, 100L);

		given(queueProperties.leaveEventTtl()).willReturn(60L);
		given(waitingQueueRepository.checkDuplicateEventProcess(eq(gameId), eq(userId), anyLong())).willReturn(true);
		given(keyProvider.getLockKey(gameId)).willReturn("lock:key");

		willAnswer(invocation -> {
			Supplier<?> action = invocation.getArgument(1);
			return action.get();
		}).given(lockManager).withLock(anyString(), any(Supplier.class));

		given(waitingQueueRepository.getMaxCapacity(gameId)).willReturn(50L);
		given(waitingQueueRepository.getCurrentUsers(gameId)).willReturn(49L);
		given(waitingQueueRepository.getNthQueueNum(gameId, 1L)).willReturn(null); // 대기자 없음
		given(waitingQueueRepository.getLastIssuedNum(gameId)).willReturn(500L);

		waitingQueueEventListener.handleUserLeave(event);

		verify(waitingQueueRepository).updateAllowedNum(gameId, 500L);
	}
}
