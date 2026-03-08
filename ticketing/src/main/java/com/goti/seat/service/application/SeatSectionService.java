package com.goti.seat.service.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.goti.seat.dto.response.SeatSectionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatSectionService {
	private final com.goti.seat.service.domain.SeatSectionService seatSectionService;

	public SeatSectionResponse create(
		UUID gradeId,
		UUID stadiumId,
		String sectionCode,
		Integer capacity
	) {
		return seatSectionService.create(gradeId, stadiumId, sectionCode, capacity);
	}
}
