package com.goti.payment.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goti.constants.OrderStatus;

public record TicketingOrderListItemResponse(
	UUID orderId,
	String orderNumber,
	OrderStatus orderStatus,
	Integer totalQuantity,
	Integer totalAmount,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	Instant orderedAt,
	UUID gameId,
	UUID stadiumId
) {
}
