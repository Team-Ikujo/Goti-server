package com.goti.ticketing.seat.dto.response;

import java.util.UUID;

public record GameSeatStatusInitResponse(
	UUID gameId,
	int createdCount,
	int skippedCount
) {
}
