package com.goti.queue.controller;

import static com.goti.global.api.ApiSuccessResponse.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goti.global.api.ApiSuccessResponse;
import com.goti.queue.dto.response.QueueEnterResponse;
import com.goti.queue.dto.response.QueueStatusResponse;
import com.goti.queue.service.application.WaitingQueueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Queue", description = "대기열 API")
@RestController
@RequestMapping("/api/v1/queues")
@RequiredArgsConstructor
public class WaitingQueueController {

	private final WaitingQueueService waitingQueueService;

	@Operation(
		summary = "대기열 진입",
		description = "특정 경기 예매에 대한 대기열 순번을 발급 API"
	)
	@PostMapping("/enter")
	public ResponseEntity<ApiSuccessResponse<QueueEnterResponse>> enterQueue(
		@RequestParam UUID gameId,
		@AuthenticationPrincipal(expression = "id") UUID userId
	) {
		return wrap(
			waitingQueueService
				.enterQueue(
					gameId,
					userId
				));
	}

	@Operation(
		summary = "대기 상태 조회",
		description = "자신의 순번과 입장 가능 여부를 조회 API"
	)
	@GetMapping("/global-status")
	public ResponseEntity<ApiSuccessResponse<QueueStatusResponse>> getStatus(
		@RequestParam String secureToken
	) {
		return wrap(
			waitingQueueService
				.getQueueStatus(secureToken));
	}

	@Operation(
		summary = "입장 토큰 검증 및 진입",
		description = "순번이 된 유저가 진입하기 위해 보안 토큰을 검증하고 진입 API"
	)
	@PostMapping("/seat/enter")
	public ResponseEntity<ApiSuccessResponse<Void>> enterSeat(
		@RequestParam String secureToken
	) {
		waitingQueueService.enterSeat(secureToken);
		return wrap(null);
	}

	@Operation(
		summary = "대기열 Heartbeat",
		description = "대기열에서 유저의 활성 상태를 유지하기 위해 주기적으로 호출 API"
	)
	@PostMapping("/heartbeat/waiting")
	public ResponseEntity<ApiSuccessResponse<Void>> heartbeatWaiting(
		@RequestParam UUID gameId,
		@AuthenticationPrincipal(expression = "id") UUID userId
	) {
		waitingQueueService.heartbeatWaiting(gameId, userId);
		return wrap(null);
	}

	@Operation(
		summary = "입장후 Heartbeat",
		description = "입장 후(좌석 선택 등) 세션 유지를 위해 주기적으로 호출 API"
	)
	@PostMapping("/heartbeat/active")
	public ResponseEntity<ApiSuccessResponse<Void>> heartbeatActive(
		@RequestParam UUID gameId,
		@AuthenticationPrincipal(expression = "id") UUID userId
	) {
		waitingQueueService.heartbeatActive(gameId, userId);
		return wrap(null);
	}

	@Operation(
		summary = "대기열 이탈",
		description = "직접 대기열에서 이탈하거나 결제를 완료했을 때 호출 API"
	)
	@DeleteMapping("/games/{gameId}")
	public ResponseEntity<ApiSuccessResponse<Void>> leaveQueue(
		@PathVariable UUID gameId,
		@AuthenticationPrincipal(expression = "id") UUID userId
	) {
		waitingQueueService.leaveQueue(gameId, userId);
		return wrap(null);
	}

	@Operation(
		summary = "대기열 초기화",
		description = "대기열 시스템의 초기 허용 인원을 설정 API"
	)
	@PostMapping("/init")
	public ResponseEntity<ApiSuccessResponse<Void>> initQueue(
		@RequestParam UUID gameId,
		@RequestParam long maxCapacity) {
		waitingQueueService.initQueue(gameId, maxCapacity);
		return wrap(null);
	}
}
