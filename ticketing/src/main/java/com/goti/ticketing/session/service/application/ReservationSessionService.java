package com.goti.ticketing.session.service.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goti.constants.messages.ErrorCode;
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

	private ReservationSessionCache create(UUID memberId, UUID gameId) {
		ReservationSessionCache reservationSession = new ReservationSessionCache(
			UUID.randomUUID(),
			LocalDateTime.now().plus(RedisKey.RESERVATION_SESSION.getTtl())
		);
		reservationSessionRedisRepository.save(memberId, gameId, reservationSession);
		return reservationSession;
	}
}
