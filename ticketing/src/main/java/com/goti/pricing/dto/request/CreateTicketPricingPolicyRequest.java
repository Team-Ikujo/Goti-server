package com.goti.pricing.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateTicketPricingPolicyRequest(
	@Schema(description = "팀 ID", example = "11111111-1111-1111-1111-111111111111")
	@NotNull(message = "팀 ID는 필수입니다.")
	UUID teamId,

	@Schema(description = "정책 시작일", example = "2026-03-01")
	@NotNull(message = "정책 시작일은 필수입니다.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate policyStartAt,

	@Schema(description = "정책 종료일", example = "2026-10-31")
	@NotNull(message = "정책 종료일은 필수입니다.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate policyEndAt
) {
}
