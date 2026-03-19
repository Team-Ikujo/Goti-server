package com.goti.ticketing.order.service.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.ticketing.domain.entity.order.OrderEntity;
import com.goti.ticketing.domain.entity.order.OrderItemEntity;
import com.goti.ticketing.order.repository.OrderItemRepository;
import com.goti.ticketing.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderExpiryService {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;

	@Transactional
	public void expire(UUID orderId) {
		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		order.expire();

		List<OrderItemEntity> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId);
		for (OrderItemEntity orderItem : orderItems) {
			orderItem.expire();
		}
	}
}
