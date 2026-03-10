package com.goti.order.service.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.game.GameScheduleEntity;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.exception.CustomException;
import com.goti.game.repository.GameScheduleRepository;
import com.goti.order.dto.request.OrderCreateRequest;
import com.goti.order.dto.response.OrderCreateResponse;
import com.goti.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private static final DateTimeFormatter ORDER_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private final GameScheduleRepository gameScheduleRepository;
	private final OrderRepository orderRepository;
	private final OrderHistoryService orderHistoryService;

	@Override
	@Transactional
	public OrderCreateResponse create(
		UUID userId,
		OrderCreateRequest request
	) {
		GameScheduleEntity gameSchedule = gameScheduleRepository.findById(request.gameId())
			.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

		OrderEntity order = OrderEntity.create(
			generateOrderNumber(),
			userId,
			gameSchedule,
			request.holdIds().size(),
			1
		);

		orderRepository.save(order);

		orderHistoryService.create(
			order,
			request.ordererName(),
			request.ordererPhone(),
			request.ordererEmail()
		);

		return OrderCreateResponse.from(
			order.getId(),
			order.getOrderNumber(),
			order.getGameSchedule().getId(),
			order.getOrderStatus(),
			order.getTotalQuantity(),
			order.getTotalAmount()
		);
	}

	private String generateOrderNumber() {
		String orderNumber;
		do {
			orderNumber = "ORD-" + LocalDateTime.now().format(ORDER_NUMBER_FORMATTER)
				+ "-" + UUID.randomUUID().toString().substring(0, 8);
		} while (orderRepository.existsByOrderNumber(orderNumber));
		return orderNumber;
	}

}
