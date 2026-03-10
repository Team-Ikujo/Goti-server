package com.goti.ticket.service.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.order.OrderItemEntity;
import com.goti.domain.entity.order.OrdererEntity;
import com.goti.exception.CustomException;
import com.goti.order.repository.OrderItemRepository;
import com.goti.order.repository.OrderHistoryRepository;
import com.goti.ticket.service.domain.TicketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketCreateService {
	private final OrderHistoryRepository orderHistoryRepository;
	private final OrderItemRepository orderItemRepository;
	private final TicketService ticketService;

	@Transactional
	public void create(OrderEntity order) {
		OrdererEntity orderer = orderHistoryRepository.findByOrder_Id(order.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		//TODO: 경기 제목 추가
		for (OrderItemEntity orderItem : orderItemRepository.findOrderItemsByOrderId(order.getId())) {
			ticketService.create(
				orderItem,
				order.getGameSchedule().getId(),
				order.getUserId(),
				orderer.getName(),
				orderer.getEmail(),
				orderer.getMobile(),
				null,
				LocalDateTime.of(order.getGameSchedule().getPlayDate(), order.getGameSchedule().getStartAt()),
				buildSeatInfo(orderItem),
				orderItem.getTicketPrice()
			);
		}
	}

	private String buildSeatInfo(OrderItemEntity orderItem) {
		return orderItem.getSeat().getSeatSection().getSectionCode() +
			" " +
			orderItem.getSeat().getRowName() +
			"-" +
			orderItem.getSeat().getSeatNum();
	}
}
