package com.goti.ticketing.seat.repository.dto;

import java.util.UUID;

public record GameAvailableSeatCount(
	UUID gameId,
	Long availableSeatCount
) {
}
