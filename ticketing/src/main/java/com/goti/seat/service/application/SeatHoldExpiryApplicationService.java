package com.goti.seat.service.application;

import com.goti.constants.SeatHoldStatus;
import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.seat.SeatHoldEntity;
import com.goti.domain.entity.seat.SeatStatusEntity;
import com.goti.exception.CustomException;
import com.goti.seat.repository.SeatHoldRepository;
import com.goti.seat.repository.SeatStatusRepository;
import com.goti.seat.service.domain.SeatHoldExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatHoldExpiryApplicationService {
	private final SeatHoldRepository seatHoldRepository;
	private final SeatStatusRepository seatStatusRepository;
	private final SeatHoldExpiryService seatHoldExpiryService;

	@Transactional
	public SeatHoldExpiryBatchResult expireHolds(int batchSize) {
		Instant now = Instant.now();
		List<SeatHoldEntity> expiredHolds = seatHoldRepository.findByStatusAndExpiredAtBeforeOrderByExpiredAtAsc(
			SeatHoldStatus.HOLDING,
			now,
			PageRequest.of(0, batchSize)
		);

		int succeeded = 0;
		int failed = 0;
		for (SeatHoldEntity seatHold : expiredHolds) {
			try {
				expireOne(seatHold, now);
				succeeded++;
			} catch (Exception e) {
				failed++;
				log.warn(
					"좌석 점유 만료 처리 실패. holdId={}, gameId={}, seatId={}, reason={}",
					seatHold.getId(),
					seatHold.getGame().getId(),
					seatHold.getSeat().getId(),
					e.getMessage()
				);
			}
		}

		return new SeatHoldExpiryBatchResult(expiredHolds.size(), succeeded, failed);
	}

	private void expireOne(
		SeatHoldEntity seatHold,
		Instant now
	) {
		UUID gameId = seatHold.getGame().getId();
		UUID seatId = seatHold.getSeat().getId();

		SeatStatusEntity seatStatus = seatStatusRepository.findByGame_IdAndSeat_Id(gameId, seatId)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_STATUS_NOT_FOUND));

		seatHoldExpiryService.expire(seatStatus, seatHold, now);
		seatStatusRepository.save(seatStatus);
		seatHoldRepository.save(seatHold);
	}
}
