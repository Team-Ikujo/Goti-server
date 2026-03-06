package com.goti.seat.service.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.seat.SeatHoldEntity;
import com.goti.domain.entity.seat.SeatStatusEntity;
import com.goti.exception.CustomException;
import com.goti.global.validation.Preconditions;
import com.goti.infra.lock.DistributedLockManager;
import com.goti.seat.repository.SeatHoldRepository;
import com.goti.seat.repository.SeatStatusRepository;
import com.goti.seat.service.command.HoldSeatCommand;
import com.goti.seat.service.command.ReleaseSeatCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldApplicationService {
	private final SeatStatusRepository seatStatusRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final DistributedLockManager distributedLockManager;

	@Value("${seat.hold.ttl-seconds}")
	private long holdTtlSeconds;

	@Transactional
	public UUID hold(HoldSeatCommand cmd) {
		String lockKey = buildLockKey(cmd.gameId(), cmd.seatId());
		return distributedLockManager.withLock(lockKey, () -> holdInLock(cmd));
	}

	@Transactional
	public UUID release(ReleaseSeatCommand cmd) {
		SeatHoldEntity seatHold = seatHoldRepository.findById(cmd.holdId())
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "좌석 점유 정보를 찾을 수 없습니다."));

		String lockKey = buildLockKey(seatHold.getGame().getId(), seatHold.getSeat().getId());
		return distributedLockManager.withLock(lockKey, () -> releaseInLock(cmd, seatHold));
	}

	private UUID holdInLock(HoldSeatCommand cmd) {
		SeatStatusEntity seatStatus = seatStatusRepository.findByGame_IdAndSeat_Id(cmd.gameId(), cmd.seatId())
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_STATUS_NOT_FOUND));

		seatStatus.hold();
		seatStatusRepository.save(seatStatus);

		SeatHoldEntity seatHold = createSeatHold(cmd, seatStatus);
		return seatHoldRepository.save(seatHold).getId();
	}

	private SeatHoldEntity createSeatHold(HoldSeatCommand cmd, SeatStatusEntity seatStatus) {
		return SeatHoldEntity.create(
			seatStatus.getSeat(),
			seatStatus.getGame(),
			cmd.userId(),
			cmd.queueTokenJti(),
			Instant.now().plusSeconds(holdTtlSeconds)
		);
	}

	private UUID releaseInLock(
		ReleaseSeatCommand cmd,
		SeatHoldEntity seatHold
	) {
		Preconditions.validate(
			seatHold.getUserId().equals(cmd.userId()),
			ErrorCode.AUTH_PERMISSION_DENIED
		);

		SeatStatusEntity seatStatus = seatStatusRepository.findByGame_IdAndSeat_Id(
			seatHold.getGame().getId(),
			seatHold.getSeat().getId()
		).orElseThrow(() -> new CustomException(ErrorCode.SEAT_STATUS_NOT_FOUND));

		seatStatus.release();
		seatHold.release();

		seatStatusRepository.save(seatStatus);
		seatHoldRepository.save(seatHold);

		return seatHold.getId();
	}

	private static String buildLockKey(UUID gameId, UUID seatId) {
		return "lock:seat:" + gameId + ":" + seatId;
	}
}
