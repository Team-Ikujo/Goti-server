package com.goti.seat.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.goti.domain.entity.seat.QSeatEntity;
import com.goti.domain.entity.seat.SeatEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<SeatEntity> findExistingSeats(UUID sectionId, String rowName, Collection<Integer> seatNumbers) {
		QSeatEntity seat = QSeatEntity.seatEntity;

		return queryFactory
			.selectFrom(seat)
			.where(
				seat.seatSection.id.eq(sectionId),
				seat.rowName.eq(rowName),
				seat.seatNum.in(seatNumbers)
			)
			.fetch();
	}
}
