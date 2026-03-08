package com.goti.seat.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goti.domain.entity.seat.SeatEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, UUID> {
	List<SeatEntity> findBySeatSection_IdAndRowNameAndSeatNumIn(UUID sectionId, String rowName, Collection<Integer> seatNums);

	long countBySeatSection_Id(UUID sectionId);
}
