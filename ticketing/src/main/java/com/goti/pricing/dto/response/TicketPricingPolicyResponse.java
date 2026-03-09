package com.goti.pricing.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.goti.domain.entity.pricing.TicketPricingPolicyEntity;

import io.swagger.v3.oas.annotations.media.Schema;

public record TicketPricingPolicyResponse(
	@Schema(description = "가격 정책 ID", example = "22222222-2222-2222-2222-222222222222")
	UUID policyId,

	@Schema(description = "팀 ID", example = "11111111-1111-1111-1111-111111111111")
	UUID teamId,

	@Schema(description = "정책 시작일", example = "2026-03-01")
	LocalDate policyStartAt,

	@Schema(description = "정책 종료일", example = "2026-10-31")
	LocalDate policyEndAt,

	@Schema(description = "활성 여부", example = "true")
	boolean isActive
) {
	public static TicketPricingPolicyResponse from(TicketPricingPolicyEntity policy) {
		return new TicketPricingPolicyResponse(
			policy.getId(),
			policy.getTeamId(),
			policy.getPolicyStartAt(),
			policy.getPolicyEndAt(),
			policy.isActive()
		);
	}
}
