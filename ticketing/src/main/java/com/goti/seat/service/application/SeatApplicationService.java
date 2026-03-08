package com.goti.seat.service.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goti.seat.dto.response.BulkCreateSeatsResponse;
import com.goti.seat.service.domain.SeatService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatApplicationService {
	private final SeatService bulkCreateSeatsService;

	public BulkCreateSeatsResponse create(
		UUID sectionId,
		String rowName,
		Integer startSeatNumber,
		Integer endSeatNumber
	) {
		bulkCreateSeatsService.create(
			sectionId,
			rowName,
			startSeatNumber,
			endSeatNumber
		);

		return BulkCreateSeatsResponse.of(
			sectionId,
			rowName,
			startSeatNumber,
			endSeatNumber
		);
	}
}
