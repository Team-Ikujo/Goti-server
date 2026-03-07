package com.goti.seat.dto.response;

import java.util.UUID;

import com.goti.domain.entity.seat.SeatGradeEntity;

public record SeatGradeResponse(
	UUID seatGradeId,
	UUID stadiumId,
	String name,
	String displayColorHex
) {
	public static SeatGradeResponse from(SeatGradeEntity seatGrade) {
		return new SeatGradeResponse(
			seatGrade.getId(),
			seatGrade.getStadiumId(),
			seatGrade.getName(),
			seatGrade.getDisplayColorHex()
		);
	}
}
