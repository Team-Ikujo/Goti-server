package com.goti.payment.dto.request.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseHistoryType {
	ALL("전체"),
	NORMAL("일반 예매"),
	RESALE("리셀 예매");

	private final String description;
}
