package com.goti.seat.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goti.domain.entity.seat.SeatEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, UUID>, SeatRepositoryCustom {

	long countBySeatSection_Id(UUID sectionId);
}
