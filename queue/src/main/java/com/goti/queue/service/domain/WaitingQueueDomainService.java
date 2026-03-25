package com.goti.queue.service.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goti.queue.dto.response.QueueStatusResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingQueueDomainService {

	public String createTokenPayload(UUID gameId, UUID userId, Long queueNumber, String activeUuid,
		LocalDateTime issuedAt) {
		long timestamp = issuedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return String.format("%s|%s|%d|%s|%d", gameId, userId, queueNumber, activeUuid, timestamp);
	}

	public String[] parseTokenPayload(String payload) {
		return payload.split("\\|");
	}

	public QueueStatusResponse calculateQueueStatus(Long myQueueNum, Long allowedNum) {
		boolean isAllowed = myQueueNum <= allowedNum;
		long waitingCount = Math.max(0, myQueueNum - allowedNum);

		return new QueueStatusResponse(
			myQueueNum,
			allowedNum,
			waitingCount,
			isAllowed
		);
	}
}
