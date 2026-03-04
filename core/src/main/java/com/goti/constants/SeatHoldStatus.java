package com.goti.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatHoldStatus {

	HOLDING("점유 중"),
	RELEASED("점유 해제"),
	CONVERTED_TO_ORDER("주문으로 전환됨");

	private final String description;
}
