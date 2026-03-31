package com.goti.user.service.auth.application.member;

import com.goti.user.domain.entity.user.MemberEntity;
import com.goti.user.dto.response.AccountCreateResponse;
import com.goti.user.service.domain.account.AccountService;
import com.goti.user.service.domain.user.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberProfileService {
	private final MemberService memberService;
	private final AccountService accountService;

	@Transactional
	public AccountCreateResponse createAccount(
		String accountNumber, String bankName, String accountHolder, UUID memberId
	) {
		MemberEntity member = memberService.getMember(memberId);
		return accountService.create(
			accountNumber, bankName, accountHolder, member
		);
	}

}
