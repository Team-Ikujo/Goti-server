package com.goti.ticketing.seat.service.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.ticketing.domain.entity.game.GameScheduleEntity;
import com.goti.ticketing.domain.entity.seat.SeatEntity;
import com.goti.ticketing.domain.entity.seat.SeatStatusEntity;
import com.goti.ticketing.game.repository.gameschedule.GameScheduleRepository;
import com.goti.ticketing.seat.dto.response.GameSeatStatusInitResponse;
import com.goti.ticketing.seat.repository.SeatRepository;
import com.goti.ticketing.seat.repository.SeatStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSeatStatusInitService {
	private final GameScheduleRepository gameScheduleRepository;
	private final SeatRepository seatRepository;
	private final SeatStatusRepository seatStatusRepository;

	@Transactional
	public GameSeatStatusInitResponse init(UUID gameId) {
		GameScheduleEntity game = gameScheduleRepository.findById(gameId)
			.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

		List<SeatEntity> seats = seatRepository.findAllByStadiumId(game.getStadiumId());
		Set<UUID> existingSeatIds = new HashSet<>(seatStatusRepository.findSeatIdsByGameId(gameId));

		List<SeatStatusEntity> newSeatStatuses = seats.stream()
			.filter(seat -> !existingSeatIds.contains(seat.getId()))
			.map(seat -> SeatStatusEntity.create(game, seat))
			.toList();

		seatStatusRepository.saveAll(newSeatStatuses);

		return new GameSeatStatusInitResponse(
			gameId,
			newSeatStatuses.size(),
			seats.size() - newSeatStatuses.size()
		);
	}
}
