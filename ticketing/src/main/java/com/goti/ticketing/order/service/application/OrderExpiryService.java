package com.goti.ticketing.order.service.application;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.goti.global.validation.Preconditions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.ticketing.domain.entity.order.OrderEntity;
import com.goti.ticketing.domain.entity.order.OrderItemEntity;
import com.goti.ticketing.domain.entity.seat.SeatHoldEntity;
import com.goti.ticketing.domain.entity.seat.SeatStatusEntity;
import com.goti.ticketing.order.repository.OrderItemRepository;
import com.goti.ticketing.order.repository.OrderRepository;
import com.goti.ticketing.seat.repository.SeatHoldRepository;
import com.goti.ticketing.seat.repository.SeatStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderExpiryService {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final SeatHoldRepository seatHoldRepository;
	private final SeatStatusRepository seatStatusRepository;

	@Transactional
	public void expire(UUID orderId) {
		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		order.expire();

		List<OrderItemEntity> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId);
		List<UUID> holdIds = orderItems.stream()
			.map(OrderItemEntity::getHoldId)
			.toList();
		List<UUID> seatIds = orderItems.stream()
			.map(orderItem -> orderItem.getSeat().getId())
			.toList();

		Map<UUID, SeatHoldEntity> seatHoldMap = seatHoldRepository.findAllWithDetailsByIdIn(holdIds).stream()
			.collect(Collectors.toMap(SeatHoldEntity::getId, seatHold -> seatHold));

		Map<UUID, SeatStatusEntity> seatStatusMap = seatStatusRepository
			.findAllByGameIdAndSeatIds(order.getGameSchedule().getId(), seatIds)
			.stream()
			.collect(Collectors.toMap(seatStatus -> seatStatus.getSeat().getId(), seatStatus -> seatStatus));

		for (OrderItemEntity orderItem : orderItems) {
			SeatHoldEntity seatHold = seatHoldMap.get(orderItem.getHoldId());
			Preconditions.validate(
				seatHold != null,
				ErrorCode.SEAT_HOLD_NOT_FOUND
			);

			SeatStatusEntity seatStatus = seatStatusMap.get(orderItem.getSeat().getId());
			Preconditions.validate(
				seatStatus != null,
				ErrorCode.SEAT_STATUS_NOT_FOUND
			);

			seatStatus.release();
			seatHold.release();

			orderItem.expire();
		}
	}
}
