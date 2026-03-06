package com.goti.seat.dto.request;

import com.goti.seat.service.command.HoldSeatCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record HoldSeatRequest(
	@NotNull(message = "경기 ID는 필수입니다.")
	UUID gameId,

	@NotNull(message = "좌석 ID는 필수입니다.")
	UUID seatId,

	@NotNull(message = "유저 ID는 필수입니다.")
	UUID userId,

	@NotBlank(message = "큐 토큰 식별자는 필수입니다.")
	String queueTokenJti
) {
	public HoldSeatCommand toCommand() {
		return new HoldSeatCommand(
			gameId,
			seatId,
			userId,
			queueTokenJti
		);
	}
}
