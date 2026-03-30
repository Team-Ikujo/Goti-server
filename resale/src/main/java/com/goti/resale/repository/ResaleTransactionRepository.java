package com.goti.resale.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.goti.resale.constants.ResaleTransactionStatus;
import com.goti.resale.domain.entity.resale.ResaleTransactionEntity;

public interface ResaleTransactionRepository extends JpaRepository<ResaleTransactionEntity, UUID> {

	List<ResaleTransactionEntity> findAllByResaleOrderId(UUID resaleOrderId);

	@Query("""
		select transaction
		from ResaleTransactionEntity transaction
		join fetch transaction.resaleOrder resaleOrder
		join fetch transaction.listing listing
		where resaleOrder.id in :orderIds
		order by resaleOrder.createdAt desc, transaction.createdAt asc
		""")
	List<ResaleTransactionEntity> findAllWithListingByResaleOrderIds(@Param("orderIds") List<UUID> orderIds);

	int countByBuyerIdAndListing_GameIdAndTransactionStatus(UUID buyerId, UUID gameId, ResaleTransactionStatus status);

}
