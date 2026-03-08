package com.goti.seat.service.domain;

import java.util.List;
import java.util.UUID;

import com.goti.domain.entity.seat.SeatEntity;

public interface BulkCreateSeatsService {
	List<SeatEntity> create(UUID sectionId, String rowName, Integer startSeatNumber, Integer endSeatNumber);
}
