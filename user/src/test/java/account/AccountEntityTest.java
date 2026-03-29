package account;

import com.goti.constants.Gender;
import com.goti.user.domain.entity.user.AccountEntity;
import com.goti.user.domain.entity.user.MemberEntity;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
public class AccountEntityTest {

	MemberEntity member;

	@BeforeEach
	void setup() {
		member = MemberEntity.create(
			"01012341234",
			"테스트회원",
			Gender.MALE,
			LocalDate.of(2026, 2, 4)
		);
		assertNotNull(member);
	}

	@Test
	void 회원_계좌_생성() {
		AccountEntity accountEntity = AccountEntity.create(
			"1002-876-543210",
			"우리은행",
			"테스트예금주",
			member
		);
		assertNotNull(accountEntity);
		log.info("account name:: {}", accountEntity.getAccountNumber());
		log.info("account holder:: {}", accountEntity.getAccountHolder());
		log.info("account bankName:: {}", accountEntity.getBankName());
	}

}
