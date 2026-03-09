package com.goti.seat.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.goti.domain.entity.seat.SeatEntity;

public interface SeatRepositoryCustom {
	List<SeatEntity> findExistingSeats(UUID sectionId, String rowName, Collection<Integer> seatNumbers);
}
