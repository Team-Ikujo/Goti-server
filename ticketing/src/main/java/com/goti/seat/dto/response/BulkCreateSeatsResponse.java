package com.goti.seat.dto.response;

import java.util.UUID;

public record BulkCreateSeatsResponse(
	UUID sectionId,
	String rowName,
	Integer startSeatNumber,
	Integer endSeatNumber,
	Integer createdCount
) {
	public static BulkCreateSeatsResponse of(
		UUID sectionId,
		String rowName,
		Integer startSeatNumber,
		Integer endSeatNumber,
		Integer createdCount
	) {
		return new BulkCreateSeatsResponse(
			sectionId,
			rowName,
			startSeatNumber,
			endSeatNumber,
			createdCount
		);
	}
}
