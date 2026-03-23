package com.goti.ticketing.session.service.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.global.validation.Preconditions;
import com.goti.infra.constants.redis.RedisKey;
import com.goti.ticketing.session.model.ReservationSessionCache;
import com.goti.ticketing.session.repository.ReservationSessionRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationSessionService {
	private final ReservationSessionRedisRepository reservationSessionRedisRepository;

	public ReservationSessionCache getOrCreate(
		UUID memberId,
		UUID gameId
	) {
		Preconditions.validate(
			memberId != null,
			ErrorCode.AUTH_INVALID
		);
		Preconditions.validate(
			gameId != null,
			ErrorCode.BAD_REQUEST
		);

		return reservationSessionRedisRepository.find(memberId, gameId)
			.orElseGet(() -> create(memberId, gameId));
	}

	public void validateActiveSession(
		UUID memberId,
		UUID gameId
	) {
		Preconditions.validate(
			memberId != null,
			ErrorCode.AUTH_INVALID
		);
		Preconditions.validate(
			gameId != null,
			ErrorCode.BAD_REQUEST
		);

		ReservationSessionCache reservationSession = reservationSessionRedisRepository.find(memberId, gameId)
			.orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_SESSION_EXPIRED));

		if (reservationSession.expiresAt().isBefore(LocalDateTime.now())) {
			reservationSessionRedisRepository.delete(memberId, gameId);
			throw new CustomException(ErrorCode.RESERVATION_SESSION_EXPIRED);
		}
	}

	private ReservationSessionCache create(UUID memberId, UUID gameId) {
		ReservationSessionCache reservationSession = new ReservationSessionCache(
			UUID.randomUUID(),
			LocalDateTime.now().plus(RedisKey.RESERVATION_SESSION.getTtl())
		);
		reservationSessionRedisRepository.save(memberId, gameId, reservationSession);
		return reservationSession;
	}
}
