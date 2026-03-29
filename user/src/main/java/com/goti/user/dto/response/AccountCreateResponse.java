package com.goti.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "계좌 생성 응답")
public record AccountCreateResponse(

	@Schema(description = "계좌번호", example = "1002-876-543210")
	String accountNumber,

	@Schema(description = "계좌 은행", example = "우리은행")
	String bankName,

	@Schema(description = "계좌 예금주", example = "홍길동")
	String accountHolder
) {
	public static AccountCreateResponse from(
		String accountNumber, String bankName, String accountHolder
	) {
		return new AccountCreateResponse(accountNumber, bankName, accountHolder);
	}
}
