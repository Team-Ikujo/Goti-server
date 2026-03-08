package com.goti.seat.service.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goti.domain.entity.seat.SeatEntity;
import com.goti.seat.dto.response.BulkCreateSeatsResponse;
import com.goti.seat.service.domain.BulkCreateSeatsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BulkCreateSeatsUseCase {
	private final BulkCreateSeatsService bulkCreateSeatsService;

	public BulkCreateSeatsResponse create(
		UUID sectionId,
		String rowName,
		Integer startSeatNumber,
		Integer endSeatNumber
	) {
		List<SeatEntity> seats = bulkCreateSeatsService.create(
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
