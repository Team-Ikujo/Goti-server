package com.goti.seat.service.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.seat.SeatHoldEntity;
import com.goti.exception.CustomException;
import com.goti.infra.lock.DistributedLockManager;
import com.goti.seat.repository.SeatHoldRepository;
import com.goti.seat.service.command.HoldSeatCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldApplicationService {
	private final SeatHoldRepository seatHoldRepository;
	private final DistributedLockManager distributedLockManager;
	private final SeatHoldTransactionalService seatHoldTransactionalService;

	public UUID hold(HoldSeatCommand cmd) {
		String lockKey = buildLockKey(cmd.gameId(), cmd.seatId());
		return distributedLockManager.withLock(lockKey, () -> seatHoldTransactionalService.hold(cmd));
	}

	public UUID release(UUID holdId, UUID userId) {
		SeatHoldEntity seatHold = seatHoldRepository.findById(holdId)
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "좌석 점유 정보를 찾을 수 없습니다."));

		String lockKey = buildLockKey(seatHold.getGame().getId(), seatHold.getSeat().getId());
		return distributedLockManager.withLock(lockKey, () -> seatHoldTransactionalService.release(holdId, userId));
	}

	private static String buildLockKey(UUID gameId, UUID seatId) {
		return "lock:seat:" + gameId + ":" + seatId;
	}
}
