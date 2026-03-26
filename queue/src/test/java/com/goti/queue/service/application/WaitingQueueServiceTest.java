package com.goti.queue.service.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.goti.exception.CustomException;
import com.goti.global.utils.TokenEncryptor;
import com.goti.queue.dto.response.QueueEnterResponse;
import com.goti.queue.dto.response.QueueStatusResponse;
import com.goti.queue.infra.config.QueueProperties;
import com.goti.queue.repository.WaitingQueueRepository;
import com.goti.queue.service.domain.WaitingQueueDomainService;

@ExtendWith(MockitoExtension.class)
class WaitingQueueServiceTest {

	@Mock
	WaitingQueueRepository waitingQueueRepository;
	@Mock
	TokenEncryptor tokenEncryptor;
	@Mock
	QueueProperties queueProperties;
	@Mock
	ApplicationEventPublisher eventPublisher;
	@Spy
	WaitingQueueDomainService domainService;

	@InjectMocks
	WaitingQueueService waitingQueueService;

	final UUID gameId = UUID.randomUUID();
	final UUID userId = UUID.randomUUID();

	@Test
	void 대기열_진입_성공() {
		given(queueProperties.enqueueDuplicateTtl()).willReturn(15L);
		given(queueProperties.waitingTtl()).willReturn(30L);
		given(waitingQueueRepository.checkDuplicateEnqueue(eq(gameId), eq(userId), anyLong())).willReturn(true);
		given(waitingQueueRepository.issueNextQueueNum(gameId)).willReturn(100L);
		given(tokenEncryptor.encrypt(anyString())).willReturn("secure-token");

		QueueEnterResponse response = waitingQueueService.enterQueue(gameId, userId);

		assertThat(response.myQueueNum()).isEqualTo(100L);
		assertThat(response.secureToken()).isEqualTo("secure-token");
		verify(waitingQueueRepository).enqueue(eq(gameId), eq(userId), eq(100L));
	}

	@Test
	void 대기열_상태_조회_성공() {
		String secureToken = "token";
		String payload = String.format("%s|%s|100|uuid|123456", gameId, userId);
		given(tokenEncryptor.decrypt(secureToken)).willReturn(payload);
		given(waitingQueueRepository.getAllowedQueueNum(gameId)).willReturn(80L);

		QueueStatusResponse response = waitingQueueService.getQueueStatus(secureToken);

		assertThat(response.myQueueNum()).isEqualTo(100L);
		assertThat(response.waitingCount()).isEqualTo(20L);
		assertThat(response.isAllowed()).isFalse();
	}

	@Test
	void 좌석_진입_성공() {
		String payload = String.format("%s|%s|100|active-uuid|123456", gameId, userId);
		given(tokenEncryptor.decrypt(anyString())).willReturn(payload);
		given(waitingQueueRepository.getAllowedQueueNum(gameId)).willReturn(100L);
		given(waitingQueueRepository.removeFromWaiting(gameId, userId)).willReturn(100L);
		given(queueProperties.activeTtl()).willReturn(1800L);

		waitingQueueService.enterSeat("token");

		verify(waitingQueueRepository).moveToActive(eq(gameId), eq(userId), eq("active-uuid"), anyLong());
		verify(waitingQueueRepository).updateCurrentUsers(gameId, 1);
	}

	@Test
	void 좌석_진입_실패_순번미달() {
		String payload = String.format("%s|%s|100|uuid|123456", gameId, userId);
		given(tokenEncryptor.decrypt(anyString())).willReturn(payload);
		given(waitingQueueRepository.getAllowedQueueNum(gameId)).willReturn(50L);

		assertThatThrownBy(() -> waitingQueueService.enterSeat("token"))
			.isInstanceOf(CustomException.class);
	}
}
