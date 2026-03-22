package com.goti.ticketing.seat.service.domain;

import java.util.List;
import java.util.UUID;

import com.goti.ticketing.domain.entity.game.GameScheduleEntity;
import com.goti.ticketing.domain.entity.seat.SeatEntity;
import com.goti.ticketing.domain.entity.seat.SeatStatusEntity;
import com.goti.ticketing.seat.dto.response.GameSeatStatusResponse;

public interface SeatStatusService {
	List<GameSeatStatusResponse> get(UUID gameId, UUID sectionId, UUID userId);

	SeatStatusEntity get(GameScheduleEntity game, SeatEntity seat);

	void cancelSale(SeatStatusEntity seatStatus);
}
