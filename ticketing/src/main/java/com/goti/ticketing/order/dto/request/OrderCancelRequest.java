package com.goti.ticketing.order.dto.request;

import java.util.List;
import java.util.UUID;

import com.goti.ticketing.constants.OrderCancellationRequestType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCancelRequest(
	@NotNull OrderCancellationRequestType requestType,
	List<UUID> orderItemIds,
	@NotBlank String idempotencyKey
) {
}
