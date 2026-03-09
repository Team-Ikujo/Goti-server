package com.goti.order.controller;

import static com.goti.global.api.ApiSuccessResponse.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goti.global.api.ApiSuccessResponse;
import com.goti.order.dto.request.CreateOrderRequest;
import com.goti.order.dto.response.CreateOrderResponse;
import com.goti.order.service.domain.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games")
public class OrderController {
	private final OrderService orderService;

	@Operation(
		summary = "주문 생성",
		description = "특정 경기의 hold 좌석 기준으로 주문을 생성하는 API"
	)
	@PostMapping("/{gameId}/orders")
	public ResponseEntity<ApiSuccessResponse<CreateOrderResponse>> create(
		@PathVariable UUID gameId,
		@RequestParam(required = false) UUID userId, // TODO: 인증 컨텍스트에서 조회
		@Valid @RequestBody CreateOrderRequest request
	) {
		return wrap(orderService.create(gameId, userId, request));
	}
}
