package com.goti.seat.controller;

import static com.goti.global.api.ApiSuccessResponse.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goti.global.api.ApiSuccessResponse;
import com.goti.seat.dto.request.CreateSeatSectionRequest;
import com.goti.seat.dto.response.SeatSectionResponse;
import com.goti.seat.service.domain.SeatSectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Seat Section", description = "좌석 구역 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stadiums")
public class SeatSectionController {
	private final SeatSectionService seatSectionService;

	@Operation(
		summary = "좌석 구역 생성",
		description = "좌석 등급에 속한 좌석 구역 생성 API"
	)
	@PostMapping("/seat-sections")
	public ResponseEntity<ApiSuccessResponse<SeatSectionResponse>> create(
		@Valid @RequestBody CreateSeatSectionRequest request
	) {
		// TODO: 관리자용 API 분리 예정
		SeatSectionResponse response = seatSectionService.create(
			request.gradeId(),
			request.stadiumId(),
			request.sectionCode(),
			request.capacity()
		);
		return wrap(response);
	}

	@Operation(
		summary = "좌석 구역 조회",
		description = "구장별 좌석 구역 목록 조회 API"
	)
	@GetMapping("/{stadiumId}/seat-sections")
	public ResponseEntity<ApiSuccessResponse<List<SeatSectionResponse>>> list(
		@AuthenticationPrincipal(expression = "id") UUID userId,
		@PathVariable UUID stadiumId
	) {
		return wrap(seatSectionService.get(stadiumId, userId));
	}
}
