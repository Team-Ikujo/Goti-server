package com.goti.ticketing.order.service.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.ticketing.constants.SeatHoldStatus;
import com.goti.ticketing.domain.entity.order.OrderEntity;
import com.goti.ticketing.domain.entity.order.OrderItemEntity;
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
		for (OrderItemEntity orderItem : orderItems) {
			seatHoldRepository.findLatestActiveHold(
				order.getGameSchedule(),
				orderItem.getSeat(),
				order.getMemberId(),
				SeatHoldStatus.HOLDING
			).ifPresent(seatHold -> {
				seatStatusRepository.findByGameAndSeat(order.getGameSchedule(), orderItem.getSeat())
					.orElseThrow(() -> new CustomException(ErrorCode.SEAT_STATUS_NOT_FOUND))
					.release();
				seatHold.release();
			});

			orderItem.expire();
		}
	}
}
