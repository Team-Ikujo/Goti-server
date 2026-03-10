package com.goti.order.repository;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.goti.constants.OrderItemStatus;
import com.goti.domain.entity.order.OrderItemEntity;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
	@Query("""
		SELECT COUNT(orderItem) > 0
			FROM OrderItemEntity orderItem
		WHERE orderItem.order.gameSchedule.id = :gameId
		  AND orderItem.seat.id IN :seatIds
		  AND orderItem.itemStatus IN :statuses
	""")
	boolean existsOrderedSeats(
		@Param("gameId") UUID gameId,
		@Param("seatIds") Collection<UUID> seatIds,
		@Param("statuses") Collection<OrderItemStatus> statuses
	);
}
