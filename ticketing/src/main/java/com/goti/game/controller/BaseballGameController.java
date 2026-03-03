package com.goti.game.controller;

import com.goti.game.dto.request.CreateGameRequest;
import com.goti.game.dto.response.GameResponse;
import com.goti.game.service.BaseballGameApplicationService;
import com.goti.global.api.ApiSuccessResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games/baseball")
public class BaseballGameController {
	private final BaseballGameApplicationService baseballGameApplicationService;

	@PostMapping
	public ResponseEntity<ApiSuccessResponse<GameResponse>> create(
		@Valid @RequestBody CreateGameRequest request
	) {
		GameResponse response = baseballGameApplicationService.create(request.toCommand());
		return ApiSuccessResponse.wrap(response);
	}
}
