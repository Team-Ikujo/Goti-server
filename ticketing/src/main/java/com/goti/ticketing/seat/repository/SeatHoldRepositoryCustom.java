package com.goti.ticketing.seat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.goti.ticketing.domain.entity.seat.SeatHoldEntity;

public interface SeatHoldRepositoryCustom {
	List<SeatHoldEntity> findAllWithDetailsByIdIn(List<UUID> holdIds);

	Optional<SeatHoldEntity> findHoldWithSeatAndGame(UUID holdId);

	List<SeatHoldEntity> findAllHoldingSeats(UUID gameId, UUID userId);
}
