package com.goti.ticketing.order.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.goti.constants.OrderStatus;
import com.goti.ticketing.constants.OrderCancellationRequestType;
import com.goti.ticketing.domain.entity.order.OrderCancellationEntity;
import com.goti.ticketing.domain.entity.order.OrderEntity;

public record OrderCancelResponse(
	UUID cancellationId,
	UUID orderId,
	OrderCancellationRequestType requestType,
	OrderStatus orderStatus,
	Integer refundAmount,
	Integer cancellationFeeAmount,
	Integer bookingFeeAmount,
	String paymentStatus,
	String paymentMethod,
	String paymentType,
	LocalDateTime refundAt,
	Integer canceledItemCount
) {
	public static OrderCancelResponse from(
		OrderCancellationEntity cancellation,
		OrderEntity order,
		Integer bookingFeeAmount,
		String paymentStatus,
		String paymentMethod,
		String paymentType,
		Integer canceledItemCount
	) {
		return new OrderCancelResponse(
			cancellation.getId(),
			order.getId(),
			cancellation.getRequestType(),
			order.getOrderStatus(),
			cancellation.getRefundAmountTotal(),
			cancellation.getFeeAmountTotal(),
			bookingFeeAmount,
			paymentStatus,
			paymentMethod,
			paymentType,
			cancellation.getCompletedAt(),
			canceledItemCount
		);
	}
}
