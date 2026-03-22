package com.goti.ticketing.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderCancellationRequestType {

	ORDER_CANCEL("전체 취소"),
	GAME_CANCELED("경기 취소"),
	SCHEDULE_CHANGED("경기 일정 변경");

	private final String description;
}
