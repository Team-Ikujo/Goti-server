package com.goti.order.service.domain;

import java.util.UUID;

import com.goti.domain.entity.game.GameScheduleEntity;
import com.goti.order.dto.request.CreateOrderRequest;
import com.goti.order.dto.response.CreateOrderResponse;

public interface OrderService {
	CreateOrderResponse create(
		GameScheduleEntity gameSchedule,
		UUID userId,
		CreateOrderRequest request,
		Integer totalQuantity,
		Integer totalAmount
	);
}
