package com.goti.ticketing.seat.dto.response;

import java.util.UUID;

import com.goti.ticketing.constants.SeatStatus;
import com.goti.ticketing.domain.entity.seat.SeatStatusEntity;

import io.swagger.v3.oas.annotations.media.Schema;

public record GameSeatStatusResponse(
	@Schema(description = "좌석 ID", example = "44444444-4444-4444-4444-444444444444")
	UUID seatId,

	@Schema(description = "좌석 구역 ID", example = "33333333-3333-3333-3333-333333333333")
	UUID sectionId,

	@Schema(description = "행 이름", example = "A")
	String rowName,

	@Schema(description = "좌석 번호", example = "12")
	Integer seatNum,

	@Schema(description = "좌석 상태", example = "AVAILABLE")
	SeatStatus status
) {
	public static GameSeatStatusResponse from(SeatStatusEntity seatStatus) {
		return new GameSeatStatusResponse(
			seatStatus.getSeat().getId(),
			seatStatus.getSeat().getSeatSection().getId(),
			seatStatus.getSeat().getRowName(),
			seatStatus.getSeat().getSeatNum(),
			seatStatus.getStatus()
		);
	}
}
