package com.goti.seat.dto.request;

import com.goti.seat.service.command.ReleaseSeatCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReleaseSeatRequest(
	@NotNull(message = "유저 ID는 필수입니다.")
	UUID userId
) {
	public ReleaseSeatCommand toCommand(UUID holdId) {
		return new ReleaseSeatCommand(holdId, userId);
	}
}
