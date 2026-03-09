package order;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.goti.domain.entity.game.GameScheduleEntity;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.order.OrdererEntity;
import com.goti.exception.FieldValidationException;

@ActiveProfiles("test")
class OrdererEntityTest {

	private OrderEntity order;
	private String name;
	private String mobile;
	private String email;

	@BeforeEach
	void setup() {
		GameScheduleEntity gameSchedule = GameScheduleEntity.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			LocalDate.of(2026, 4, 1),
			LocalTime.of(18, 30)
		);

		order = OrderEntity.create(
			"ORD-20260309-0001",
			UUID.randomUUID(),
			gameSchedule,
			2,
			24000
		);
		name = "홍길동";
		mobile = "01012345678";
		email = "orderer@goti.com";
	}

	@Test
	void 구매자정보_생성_성공() {
		OrdererEntity orderer = OrdererEntity.create(order, name, mobile, email);

		assertThat(orderer.getOrder()).isEqualTo(order);
		assertThat(orderer.getName()).isEqualTo(name);
		assertThat(orderer.getMobile()).isEqualTo(mobile);
		assertThat(orderer.getEmail()).isEqualTo(email);
	}

	@Test
	void 구매자정보_생성_실패_이름_공백() {
		assertThatThrownBy(
			() -> OrdererEntity.create(order, " ", mobile, email)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("구매자 이름은 비어 있을 수 없습니다.");
	}

	@Test
	void 구매자정보_생성_실패_연락처_공백() {
		assertThatThrownBy(
			() -> OrdererEntity.create(order, name, " ", email)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("구매자 연락처는 비어 있을 수 없습니다.");
	}

	@Test
	void 구매자정보_생성_실패_이메일_공백() {
		assertThatThrownBy(
			() -> OrdererEntity.create(order, name, mobile, " ")
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("구매자 이메일은 비어 있을 수 없습니다.");
	}
}
