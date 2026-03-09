package com.goti.order.service.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.SeatHoldStatus;
import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.game.GameScheduleEntity;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.seat.SeatHoldEntity;
import com.goti.exception.CustomException;
import com.goti.game.repository.GameScheduleRepository;
import com.goti.global.validation.Preconditions;
import com.goti.order.dto.request.CreateOrderRequest;
import com.goti.order.dto.response.CreateOrderResponse;
import com.goti.order.repository.OrderRepository;
import com.goti.seat.repository.SeatHoldRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private static final DateTimeFormatter ORDER_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private final GameScheduleRepository gameScheduleRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final OrderRepository orderRepository;
	private final OrdererService ordererService;

	@Override
	@Transactional
	public CreateOrderResponse create(
		UUID userId,
		CreateOrderRequest request
	) {
		validateDuplicateHoldIds(request.holdIds());

		GameScheduleEntity gameSchedule = gameScheduleRepository.findById(request.gameId())
			.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

		List<SeatHoldEntity> holds = seatHoldRepository.findAllById(request.holdIds());
		validateHolds(request.holdIds(), holds, request.gameId(), userId);

		OrderEntity order = OrderEntity.create(
			generateOrderNumber(),
			userId,
			gameSchedule,
			holds.size(),
			1
		);

		orderRepository.save(order);

		ordererService.create(
			order,
			request.ordererName(),
			request.ordererPhone(),
			request.ordererEmail()
		);

		return CreateOrderResponse.from(
			order.getId(),
			order.getOrderNumber(),
			order.getGameSchedule().getId(),
			order.getOrderStatus(),
			order.getTotalQuantity(),
			order.getTotalAmount()
		);
	}

	private void validateDuplicateHoldIds(List<UUID> holdIds) {
		Preconditions.validate(
			holdIds.size() == new HashSet<>(holdIds).size(),
			ErrorCode.DUPLICATE_HOLD_ID_REQUEST
		);
	}

	private void validateHolds(
		List<UUID> requestedHoldIds,
		List<SeatHoldEntity> holds,
		UUID gameId,
		UUID userId
	) {
		Preconditions.validate(
			holds.size() == requestedHoldIds.size(),
			ErrorCode.SEAT_HOLD_NOT_FOUND
		);

		for (SeatHoldEntity hold : holds) {
			Preconditions.validate(
				hold.getUserId().equals(userId),
				ErrorCode.AUTH_PERMISSION_DENIED
			);
			Preconditions.validate(
				hold.getGameSchedule().getId().equals(gameId),
				ErrorCode.SEAT_HOLD_GAME_MISMATCH
			);
			Preconditions.validate(
				hold.getStatus() == SeatHoldStatus.HOLDING,
				ErrorCode.SEAT_HOLD_STATUS_INVALID
			);
			Preconditions.validate(
				hold.getExpiredAt().isAfter(LocalDateTime.now()),
				ErrorCode.SEAT_HOLD_EXPIRED
			);
		}
	}

	private String generateOrderNumber() {
		String orderNumber = "ORD-" + LocalDateTime.now().format(ORDER_NUMBER_FORMATTER)
			+ "-" + UUID.randomUUID().toString().substring(0, 8);

		if (orderRepository.existsByOrderNumber(orderNumber)) {
			return generateOrderNumber();
		}

		return orderNumber;
	}
}
