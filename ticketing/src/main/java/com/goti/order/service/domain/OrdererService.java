package com.goti.order.service.domain;

import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.order.OrdererEntity;

public interface OrdererService {
	OrdererEntity create(
		OrderEntity order,
		String name,
		String phone,
		String email
	);
}
