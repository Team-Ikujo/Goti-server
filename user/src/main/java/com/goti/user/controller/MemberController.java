package com.goti.user.controller;

import com.goti.global.api.ApiSuccessResponse;
import com.goti.user.dto.request.AccountCreateRequest;
import com.goti.user.dto.response.AccountCreateResponse;
import com.goti.user.service.auth.application.member.MemberProfileService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.goti.global.api.ApiSuccessResponse.wrap;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberProfileService memberProfileService;

	@PostMapping("/accounts")
	public ResponseEntity<ApiSuccessResponse<AccountCreateResponse>> createAccount(
		@AuthenticationPrincipal(expression = "id") UUID memberId,
		@RequestBody @Valid AccountCreateRequest request
	) {
		return wrap(
			memberProfileService.createAccount(
				request.accountNumber(),
				request.bankName(),
				request.accountHolder(),
				memberId
			)
		);
	}
}
