package payment;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import com.goti.constants.PaymentMethod;
import com.goti.constants.PaymentStatus;
import com.goti.constants.PaymentType;
import com.goti.domain.entity.game.GameScheduleEntity;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.payment.PaymentEntity;
import com.goti.exception.FieldValidationException;

@ActiveProfiles("test")
class PaymentEntityTest {

	private OrderEntity order;
	private Integer paymentAmount;
	private String idempotencyKey;

	@BeforeEach
	void setup() {
		GameScheduleEntity gameSchedule = createGameSchedule();
		order = OrderEntity.create("ORD-20260310-0001", UUID.randomUUID(), gameSchedule, 2, 24000);
		paymentAmount = 24000;
		idempotencyKey = "payment-idempotency-key";
	}

	private GameScheduleEntity createGameSchedule() {
		UUID homeTeamId = UUID.randomUUID();
		UUID awayTeamId = UUID.randomUUID();
		UUID stadiumId = UUID.randomUUID();
		LocalDate playDate = LocalDate.of(2026, 4, 1);
		LocalTime startAt = LocalTime.of(18, 30);

		try {
			Method createMethod = GameScheduleEntity.class.getMethod(
				"create",
				UUID.class,
				UUID.class,
				UUID.class,
				LocalDate.class,
				LocalTime.class
			);
			return (GameScheduleEntity) createMethod.invoke(
				null,
				homeTeamId,
				awayTeamId,
				stadiumId,
				playDate,
				startAt
			);
		} catch (NoSuchMethodException ignored) {
			return createGameScheduleWithDateTime(homeTeamId, awayTeamId, stadiumId, playDate, startAt);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("GameScheduleEntity 생성에 실패했습니다.", e);
		}
	}

	private GameScheduleEntity createGameScheduleWithDateTime(
		UUID homeTeamId,
		UUID awayTeamId,
		UUID stadiumId,
		LocalDate playDate,
		LocalTime startAt
	) {
		try {
			Method createMethod = GameScheduleEntity.class.getMethod(
				"create",
				UUID.class,
				UUID.class,
				UUID.class,
				LocalDateTime.class
			);
			return (GameScheduleEntity) createMethod.invoke(
				null,
				homeTeamId,
				awayTeamId,
				stadiumId,
				LocalDateTime.of(playDate, startAt)
			);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("지원되는 GameScheduleEntity.create 시그니처를 찾지 못했습니다.", e);
		}
	}

	@Test
	void 결제_생성_성공() {
		PaymentEntity payment = PaymentEntity.create(
			order,
			null,
			PaymentType.PAYMENT,
			PaymentMethod.CARD,
			paymentAmount,
			"MOCK",
			"pg-tid-001",
			idempotencyKey
		);

		assertThat(payment.getOrder()).isEqualTo(order);
		assertThat(payment.getCancellation()).isNull();
		assertThat(payment.getPaymentType()).isEqualTo(PaymentType.PAYMENT);
		assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
		assertThat(payment.getPaymentAmount()).isEqualTo(paymentAmount);
		assertThat(payment.getPgProvider()).isEqualTo("MOCK");
		assertThat(payment.getPgTid()).isEqualTo("pg-tid-001");
		assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThat(payment.getPaidAt()).isNull();
		assertThat(payment.getIdempotencyKey()).isEqualTo(idempotencyKey);
	}

	@Test
	void 결제_생성_실패_결제처리유형_null() {
		assertThatThrownBy(
			() -> PaymentEntity.create(
				order,
				null,
				null,
				PaymentMethod.CARD,
				paymentAmount,
				"MOCK",
				"pg-tid-001",
				idempotencyKey
			)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("결제 처리 유형은 필수입니다.");
	}

	@Test
	void 결제_생성_실패_결제금액_0이하() {
		assertThatThrownBy(
			() -> PaymentEntity.create(
				order,
				null,
				PaymentType.PAYMENT,
				PaymentMethod.CARD,
				0,
				"MOCK",
				"pg-tid-001",
				idempotencyKey
			)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("결제 금액은 0원보다 커야 합니다.");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "   "})
	void 결제_생성_실패_멱등키_공백(String invalidIdempotencyKey) {
		assertThatThrownBy(
			() -> PaymentEntity.create(
				order,
				null,
				PaymentType.PAYMENT,
				PaymentMethod.CARD,
				paymentAmount,
				"MOCK",
				"pg-tid-001",
				invalidIdempotencyKey
			)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("멱등 키는 비어 있을 수 없습니다.");
	}
}
