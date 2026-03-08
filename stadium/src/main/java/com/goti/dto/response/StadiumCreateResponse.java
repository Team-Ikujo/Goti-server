package com.goti.dto.response;

public record StadiumCreateResponse(
	String stadiumName,
	String location,
	String city,
	String district,
	String roadAddress,
	int totalSeats
) {
	public static StadiumCreateResponse of(
		String stadiumName,
		String location,
		String city,
		String district,
		String roadAddress,
		int totalSeats
	) {
		return new StadiumCreateResponse(
			stadiumName, location, city, district, roadAddress, totalSeats
		);
	}
}
