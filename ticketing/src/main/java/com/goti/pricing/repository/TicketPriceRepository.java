package com.goti.pricing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.goti.domain.entity.pricing.TicketPriceEntity;
import com.goti.domain.entity.pricing.TicketPricingPolicyEntity;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPriceEntity, UUID> {
	@Query("""
		SELECT COUNT(price) > 0
		FROM TicketPriceEntity price
		WHERE price.policy = :policy
		  AND price.grade.id = :gradeId
		  AND price.dayType = :dayType
		  AND price.matchType = :matchType
	""")
	boolean existsDuplicateTicketPrice(
		@Param("policy") TicketPricingPolicyEntity policy,
		@Param("gradeId") UUID gradeId,
		@Param("dayType") com.goti.constants.TicketPricingDayType dayType,
		@Param("matchType") com.goti.constants.TicketPricingMatchType matchType
	);
}
