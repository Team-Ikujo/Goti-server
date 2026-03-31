package com.goti.user.service.domain.account;

import com.goti.user.domain.entity.user.MemberEntity;
import com.goti.user.dto.response.AccountCreateResponse;

public interface AccountService {

	AccountCreateResponse register(
		String accountNumber, String bankName, String accountHolder, MemberEntity member
	);
}
