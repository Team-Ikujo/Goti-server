package com.goti.order.service.domain;

import java.util.UUID;

import com.goti.order.dto.request.CreateOrderRequest;
import com.goti.order.dto.response.CreateOrderResponse;

public interface OrderService {
	CreateOrderResponse create(
		UUID userId,
		CreateOrderRequest request
	);
}
