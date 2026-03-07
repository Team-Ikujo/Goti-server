package com.goti.seat.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSeatGradeRequest(
	@NotNull(message = "구장 ID는 필수입니다.")
	UUID stadiumId,

	@NotBlank(message = "좌석 등급명은 필수입니다.")
	String name,

	String displayColorHex
) {
}
