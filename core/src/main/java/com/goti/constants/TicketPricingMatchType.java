package com.goti.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketPricingMatchType {

	PRE_SEASON("프리시즌"),
	REGULAR("정규리그"),
	POST_SEASON("포스트시즌");

	private final String description;
}
