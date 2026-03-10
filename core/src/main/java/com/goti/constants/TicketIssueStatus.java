package com.goti.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketIssueStatus {

	ISSUED("발행됨"),
	USED("사용됨"),
	CANCELLED("취소됨"),
	RESALED("리셀");

	private final String description;
}
