package com.goti.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketType {

	ADULT("성인"),
	YOUTH("청소년"),
	CHILD("어린이");

	private final String description;
}
