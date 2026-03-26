package com.goti.queue.service.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.goti.queue.dto.response.QueueStatusResponse;

class WaitingQueueDomainServiceTest {

	final WaitingQueueDomainService domainService = new WaitingQueueDomainService();

	@Test
	void 토큰_페이로드_생성_파싱_검증() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Long queueNum = 12345L;
		String activeUuid = UUID.randomUUID().toString();
		LocalDateTime now = LocalDateTime.now();

		String payload = domainService.createTokenPayload(gameId, userId, queueNum, activeUuid, now);
		String[] parts = domainService.parseTokenPayload(payload);

		assertThat(parts).hasSize(5);
		assertThat(parts[0]).isEqualTo(gameId.toString());
		assertThat(parts[1]).isEqualTo(userId.toString());
		assertThat(parts[2]).isEqualTo(queueNum.toString());
		assertThat(parts[3]).isEqualTo(activeUuid);
	}

	@Test
	void 대기상태_계산_입장가능() {
		Long myQueueNum = 100L;
		Long allowedQueueNum = 150L;

		QueueStatusResponse response = domainService.calculateQueueStatus(myQueueNum, allowedQueueNum);

		assertThat(response.isAllowed()).isTrue();
		assertThat(response.waitingCount()).isZero();
	}

	@Test
	void 대기상태_계산_대기중() {
		Long myQueueNum = 200L;
		Long allowedQueueNum = 150L;

		QueueStatusResponse response = domainService.calculateQueueStatus(myQueueNum, allowedQueueNum);

		assertThat(response.isAllowed()).isFalse();
		assertThat(response.waitingCount()).isEqualTo(50L);
	}
}
