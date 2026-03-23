package com.goti.ticketing.infra.api.dto;

public record PaymentCancelResponse(
	PaymentResponseData data
) {
	public record PaymentResponseData(
		String paymentStatus,
		String paymentMethod,
		String paymentType
	) {
	}
}
