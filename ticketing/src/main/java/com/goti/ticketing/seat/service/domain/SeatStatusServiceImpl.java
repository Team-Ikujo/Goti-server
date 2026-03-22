package com.goti.ticketing.seat.service.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.global.validation.Preconditions;
import com.goti.ticketing.domain.entity.game.GameScheduleEntity;
import com.goti.ticketing.domain.entity.seat.SeatEntity;
import com.goti.ticketing.domain.entity.seat.SeatStatusEntity;
import com.goti.ticketing.seat.dto.response.GameSeatStatusResponse;
import com.goti.ticketing.seat.repository.SeatStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatStatusServiceImpl implements SeatStatusService {
	private final SeatStatusRepository seatStatusRepository;

	@Override
	@Transactional(readOnly = true)
	public List<GameSeatStatusResponse> get(
		UUID gameId,
		UUID sectionId,
		UUID userId
	) {
		Preconditions.validate(
			userId != null,
			ErrorCode.AUTH_INVALID
		);

		return seatStatusRepository.findSeatStatuses(gameId, sectionId).stream()
			.map(GameSeatStatusResponse::from)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SeatStatusEntity get(GameScheduleEntity game, SeatEntity seat) {
		return seatStatusRepository.findByGameAndSeat(game, seat)
			.orElseThrow(() -> new CustomException(ErrorCode.SEAT_STATUS_NOT_FOUND));
	}

	@Override
	@Transactional
	public void cancelSale(SeatStatusEntity seatStatus) {
		seatStatus.cancelSale();
	}
}
