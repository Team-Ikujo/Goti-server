package com.goti.order.service.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.domain.entity.game.GameScheduleEntity;
import com.goti.exception.CustomException;
import com.goti.game.repository.GameScheduleRepository;
import com.goti.order.dto.request.CreateOrderRequest;
import com.goti.order.dto.response.CreateOrderResponse;
import com.goti.order.service.domain.OrderService;
import com.goti.constants.messages.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {
	private final GameScheduleRepository gameScheduleRepository;
	private final OrderService orderService;

	@Transactional
	public CreateOrderResponse create(UUID userId, CreateOrderRequest request) {
		GameScheduleEntity gameSchedule = gameScheduleRepository.findById(request.gameId())
			.orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

		return orderService.create(
			gameSchedule,
			userId,
			request,
			request.holdIds().size(),
			1
		);
	}
}
