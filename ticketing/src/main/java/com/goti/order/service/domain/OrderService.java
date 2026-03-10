package com.goti.order.service.domain;

import java.util.UUID;

import com.goti.order.dto.request.OrderCreateRequest;
import com.goti.order.dto.response.OrderCreateResponse;

public interface OrderService {
	OrderCreateResponse create(
		UUID userId,
		OrderCreateRequest request
	);
}
