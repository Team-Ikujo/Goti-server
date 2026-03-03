package com.goti.dto.request;

import com.goti.constants.OAuthProvider;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

	@NotBlank(message = "로그인 수단은 필수 항목입니다.")
	OAuthProvider provider,

	@NotBlank(message = "인증 코드는 필수 항목입니다.")
	String authCode
) {
}
