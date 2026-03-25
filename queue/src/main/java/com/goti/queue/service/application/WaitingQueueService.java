package com.goti.queue.service.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.global.utils.TokenEncryptor;
import com.goti.queue.dto.WaitingQueueLeaveEvent;
import com.goti.queue.dto.response.QueueEnterResponse;
import com.goti.queue.dto.response.QueueStatusResponse;
import com.goti.queue.infra.config.QueueProperties;
import com.goti.queue.repository.WaitingQueueRepository;
import com.goti.queue.service.domain.WaitingQueueDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

	private final WaitingQueueRepository waitingQueueRepository;
	private final WaitingQueueDomainService domainService;
	private final TokenEncryptor tokenEncryptor;
	private final QueueProperties queueProperties;
	private final ApplicationEventPublisher eventPublisher;

	public QueueEnterResponse enterQueue(UUID gameId, UUID userId) {
		boolean isFirst = waitingQueueRepository.checkDuplicateEnqueue(gameId, userId,
			queueProperties.enqueueDuplicateTtl());
		if (!isFirst) {
			throw new CustomException(ErrorCode.ALREADY_IN_QUEUE);
		}

		Long queueNumber = waitingQueueRepository.issueNextQueueNum(gameId);
		waitingQueueRepository.enqueue(gameId, userId, queueNumber);
		waitingQueueRepository.renewWaitingStatus(gameId, userId, queueProperties.waitingTtl());

		String activeUuid = UUID.randomUUID().toString();
		LocalDateTime issuedAt = LocalDateTime.now();
		String rawPayload = domainService.createTokenPayload(gameId, userId, queueNumber, activeUuid, issuedAt);
		String secureToken = tokenEncryptor.encrypt(rawPayload);

		return new QueueEnterResponse(secureToken, queueNumber, gameId);
	}

	public QueueStatusResponse getQueueStatus(String secureToken) {
		String payload = tokenEncryptor.decrypt(secureToken);
		String[] parts = domainService.parseTokenPayload(payload);

		UUID gameId = UUID.fromString(parts[0]);
		Long myQueueNum = Long.parseLong(parts[2]);

		Long allowedNum = waitingQueueRepository.getAllowedQueueNum(gameId);
		return domainService.calculateQueueStatus(myQueueNum, allowedNum);
	}

	public void enterSeat(String secureToken) {
		String payload = tokenEncryptor.decrypt(secureToken);
		String[] parts = domainService.parseTokenPayload(payload);

		UUID gameId = UUID.fromString(parts[0]);
		UUID userId = UUID.fromString(parts[1]);
		long queueNumber = Long.parseLong(parts[2]);
		String activeUuid = parts[3];

		Long allowedNum = waitingQueueRepository.getAllowedQueueNum(gameId);
		if (queueNumber > allowedNum) {
			throw new CustomException(ErrorCode.QUEUE_NOT_ALLOWED_YET);
		}

		Long removedQueueNum = waitingQueueRepository.removeFromWaiting(gameId, userId);
		if (removedQueueNum != null) {
			waitingQueueRepository.moveToActive(gameId, userId, activeUuid, queueProperties.activeTtl());
			waitingQueueRepository.incrementCurrentUsers(gameId, 1);
		} else {
			boolean isAlreadyActive = waitingQueueRepository
				.renewActiveStatus(gameId, userId, queueProperties.activeTtl());
			if (!isAlreadyActive) {
				log.warn("대기열 세션이 만료되었거나 비정상입니다.");
				throw new CustomException(ErrorCode.QUEUE_SESSION_EXPIRED);
			}
		}
	}

	public void heartbeatWaiting(UUID gameId, UUID userId) {
		boolean renewed = waitingQueueRepository.renewWaitingStatus(gameId, userId, queueProperties.waitingTtl());
		if (!renewed) {
			throw new CustomException(ErrorCode.QUEUE_SESSION_EXPIRED);
		}
	}

	public void heartbeatActive(UUID gameId, UUID userId) {
		boolean renewed = waitingQueueRepository.renewActiveStatus(gameId, userId, queueProperties.activeTtl());
		if (!renewed) {
			throw new CustomException(ErrorCode.TICKETING_SESSION_EXPIRED);
		}
	}

	public void leaveQueue(UUID gameId, UUID userId) {
		Long removedQueueNum = waitingQueueRepository.removeFromWaiting(gameId, userId);
		boolean removedFromActive = waitingQueueRepository.removeFromActive(gameId, userId);

		eventPublisher.publishEvent(new WaitingQueueLeaveEvent(gameId, userId, removedFromActive, removedQueueNum));
	}

	public void initQueue(UUID gameId, long maxCapacity) {
		waitingQueueRepository.initializeQueueStatus(gameId, maxCapacity);
		log.info("대기열 초기화 완료. gameId: {}, maxCapacity: {}", gameId, maxCapacity);
	}
}
