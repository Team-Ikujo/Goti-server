package com.goti.payment.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.goti.constants.OrderStatus;
import com.goti.payment.constants.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 정보 조회 응답")
public record PaymentDetailResponse(
	@Schema(description = "주문 ID", example = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
	UUID orderId,

	@Schema(description = "결제 수단", example = "CARD")
	PaymentMethod paymentMethod,

	@Schema(description = "주문 상태", example = "CONFIRMED")
	OrderStatus orderStatus,

	@Schema(description = "결제 일시")
	LocalDateTime paidAt,

	@Schema(description = "수령 방식", example = "모바일 티켓")
	String receiptMethod,

	@Schema(description = "결제 금액", example = "24000")
	Integer paymentAmount
) {
	public static PaymentDetailResponse of(
		UUID orderId,
		PaymentMethod paymentMethod,
		OrderStatus orderStatus,
		LocalDateTime paidAt,
		String receiptMethod,
		Integer paymentAmount
	) {
		return new PaymentDetailResponse(
			orderId,
			paymentMethod,
			orderStatus,
			paidAt,
			receiptMethod,
			paymentAmount
		);
	}
}
