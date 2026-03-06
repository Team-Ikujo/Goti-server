package com.goti.seat.dto.response;

import java.util.UUID;

public record HoldSeatResponse(
	UUID holdId
) {
	public static HoldSeatResponse from(UUID holdId) {
		return new HoldSeatResponse(holdId);
	}
}
