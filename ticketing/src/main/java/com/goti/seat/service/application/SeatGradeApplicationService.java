package com.goti.seat.service.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.seat.SeatGradeEntity;
import com.goti.global.validation.Preconditions;
import com.goti.seat.dto.response.SeatGradeResponse;
import com.goti.seat.repository.SeatGradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatGradeApplicationService {
	private final SeatGradeRepository seatGradeRepository;

	@Transactional
	public SeatGradeResponse create(
		UUID stadiumId,
		String name,
		String displayColorHex
	) {
		Preconditions.validate(
			!seatGradeRepository.existsByStadiumIdAndName(stadiumId, name),
			ErrorCode.SEAT_GRADE_ALREADY_EXISTS
		);

		SeatGradeEntity seatGrade = SeatGradeEntity.create(stadiumId, name, displayColorHex);
		SeatGradeEntity savedSeatGrade = seatGradeRepository.save(seatGrade);

		return SeatGradeResponse.from(savedSeatGrade);
	}

	@Transactional(readOnly = true)
	public List<SeatGradeResponse> getSeatGrades(UUID stadiumId) {
		// TODO: 유저 인증 여부 확인
		// Preconditions.validate(userId != null, ErrorCode.AUTH_INVALID);

		return seatGradeRepository.findAllByStadiumId(stadiumId).stream()
			.map(SeatGradeResponse::from)
			.toList();
	}
}
