package com.goti.seat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goti.domain.entity.seat.SeatStatusEntity;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatusEntity, UUID> {
	List<SeatStatusEntity> findAllByGame_IdAndSeat_SeatSection_Id(UUID gameId, UUID sectionId);
	Optional<SeatStatusEntity> findByGame_IdAndSeat_Id(UUID gameId, UUID seatId);
}
