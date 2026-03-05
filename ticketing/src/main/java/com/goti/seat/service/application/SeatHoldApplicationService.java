package com.goti.seat.service.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.seat.SeatHoldEntity;
import com.goti.domain.entity.seat.SeatStatusEntity;
import com.goti.exception.CustomException;
import com.goti.infra.lock.DistributedLockManager;
import com.goti.seat.repository.SeatHoldRepository;
import com.goti.seat.repository.SeatStatusRepository;
import com.goti.seat.service.command.HoldSeatCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldApplicationService {
	private final SeatStatusRepository seatStatusRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final DistributedLockManager distributedLockManager;

	@Transactional
	public UUID hold(HoldSeatCommand cmd) {
		String lockKey = buildLockKey(cmd.gameId(), cmd.seatId());
		return distributedLockManager.withLock(lockKey, () -> holdInLock(cmd));
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
			cmd.expiredAt()
		);
	}

	private static String buildLockKey(UUID gameId, UUID seatId) {
		return "lock:seat:" + gameId + ":" + seatId;
	}
}
