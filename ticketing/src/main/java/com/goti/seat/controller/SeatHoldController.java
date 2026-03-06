package com.goti.seat.controller;

import com.goti.global.api.ApiSuccessResponse;
import com.goti.seat.dto.request.HoldSeatRequest;
import com.goti.seat.dto.request.ReleaseSeatRequest;
import com.goti.seat.dto.response.HoldSeatResponse;
import com.goti.seat.dto.response.ReleaseSeatResponse;
import com.goti.seat.service.application.SeatHoldApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.goti.global.api.ApiSuccessResponse.wrap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seats/holds")
public class SeatHoldController {
	private final SeatHoldApplicationService seatHoldApplicationService;

	@PostMapping
	public ResponseEntity<ApiSuccessResponse<HoldSeatResponse>> hold(
		@Valid @RequestBody HoldSeatRequest request
	) {
		HoldSeatResponse response = HoldSeatResponse.from(
			seatHoldApplicationService.hold(request.toCommand())
		);
		return wrap(response);
	}

	@DeleteMapping("/{holdId}")
	public ResponseEntity<ApiSuccessResponse<ReleaseSeatResponse>> release(
		@PathVariable UUID holdId,
		@Valid @RequestBody ReleaseSeatRequest request
	) {
		ReleaseSeatResponse response = ReleaseSeatResponse.from(
			seatHoldApplicationService.release(request.toCommand(holdId))
		);
		return wrap(response);
	}
}
