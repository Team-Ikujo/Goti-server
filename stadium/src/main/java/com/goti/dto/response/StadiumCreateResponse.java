package com.goti.dto.response;

import java.util.UUID;

public record StadiumCreateResponse(
	UUID stadiumId,
	String stadiumName,
	String location,
	String city,
	String district,
	String roadAddress,
	int totalSeats
) {
	public static StadiumCreateResponse from(
		UUID stadiumId,
		String stadiumName,
		String location,
		String city,
		String district,
		String roadAddress,
		int totalSeats
	) {
		return new StadiumCreateResponse(
			stadiumId, stadiumName, location, city, district, roadAddress, totalSeats
		);
	}
}
