package com.goti.seat.dto.response;

import java.util.UUID;

public record ReleaseSeatResponse(
	UUID holdId
) {
	public static ReleaseSeatResponse from(UUID holdId) {
		return new ReleaseSeatResponse(holdId);
	}
}
