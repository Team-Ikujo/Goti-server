package com.goti.seat.service.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.goti.seat.dto.response.SeatGradeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatGradeService {
	private final com.goti.seat.service.domain.SeatGradeService seatGradeService;

	public SeatGradeResponse create(
		UUID stadiumId,
		String name,
		String displayColorHex
	) {
		return seatGradeService.create(stadiumId, name, displayColorHex);
	}

	public List<SeatGradeResponse> get(UUID stadiumId) {
		return seatGradeService.get(stadiumId);
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
