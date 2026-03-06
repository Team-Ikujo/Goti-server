package com.goti.seat.dto.request;

import com.goti.seat.service.command.ReleaseSeatCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReleaseSeatRequest(
	@Schema(description = "점유 해제를 요청하는 유저 ID", example = "33333333-3333-3333-3333-333333333333")
	@NotNull(message = "유저 ID는 필수입니다.")
	UUID userId
) {
	public ReleaseSeatCommand toCommand(UUID holdId) {
		return new ReleaseSeatCommand(holdId, userId);
	}
}
