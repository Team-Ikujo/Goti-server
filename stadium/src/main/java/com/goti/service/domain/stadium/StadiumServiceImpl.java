package com.goti.service.domain.stadium;

import com.goti.domain.entity.stadium.StadiumEntity;
import com.goti.dto.response.StadiumCreateResponse;
import com.goti.repository.StadiumRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService {

	private final StadiumRepository stadiumRepository;

	@Override
	public StadiumCreateResponse create(
		String stadiumName,
		String location,
		String city,
		String district,
		String roadAddress,
		BigDecimal latitude,
		BigDecimal longitude,
		Integer totalSeats,
		Map<String, Object> seatMapConfig
	) {
		StadiumEntity stadium = StadiumEntity.create(
			stadiumName,
			location,
			city,
			district,
			roadAddress,
			latitude,
			longitude,
			totalSeats,
			seatMapConfig
		);
		stadiumRepository.save(stadium);

		return StadiumCreateResponse.of(
			stadium.getId(),
			stadium.getStadiumName(),
			stadium.getLocation(),
			stadium.getCity(),
			stadium.getDistrict(),
			stadium.getRoadAddress(),
			stadium.getTotalSeats()
		);
	}
}
