package com.goti.ticketing.session.service.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.global.validation.Preconditions;
import com.goti.infra.constants.redis.RedisKey;
import com.goti.ticketing.domain.entity.seat.SeatHoldEntity;
import com.goti.ticketing.domain.entity.seat.SeatStatusEntity;
import com.goti.ticketing.seat.service.domain.SeatStatusService;
import com.goti.ticketing.session.model.ReservationSessionCache;
import com.goti.ticketing.session.repository.ReservationSessionRedisRepository;
import com.goti.ticketing.seat.repository.SeatHoldRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationSessionService {
	private final ReservationSessionRedisRepository reservationSessionRedisRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatStatusService seatStatusService;

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

		ReservationSessionCache reservationSession = reservationSessionRedisRepository.find(memberId, gameId)
			.orElseGet(() -> create(memberId, gameId));

		return reservationSession;
	}

	@Transactional
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
			releaseHeldSeats(memberId, gameId);
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

	private void releaseHeldSeats(UUID memberId, UUID gameId) {
		List<SeatHoldEntity> seatHolds = seatHoldRepository.findAllActiveByGameIdAndUserId(gameId, memberId);
		if (seatHolds.isEmpty()) {
			return;
		}

		List<UUID> seatIds = seatHolds.stream()
			.map(seatHold -> seatHold.getSeat().getId())
			.toList();

		Map<UUID, SeatStatusEntity> seatStatuses = seatStatusService.getByGameIdAndSeatIds(gameId, seatIds);

		for (SeatHoldEntity seatHold : seatHolds) {
			SeatStatusEntity seatStatus = seatStatuses.get(seatHold.getSeat().getId());
			if (seatStatus != null) {
				seatStatusService.release(seatStatus);
			}
			seatHold.release();
		}
	}
}
