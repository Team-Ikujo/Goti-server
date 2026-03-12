package com.goti.payment.service.domain;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.OrderStatus;
import com.goti.constants.PaymentMethod;
import com.goti.constants.PaymentType;
import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.order.OrderItemEntity;
import com.goti.domain.entity.payment.PaymentEntity;
import com.goti.exception.CustomException;
import com.goti.global.validation.Preconditions;
import com.goti.order.repository.OrderItemRepository;
import com.goti.order.repository.OrderRepository;
import com.goti.payment.dto.response.PaymentResponse;
import com.goti.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private static final String MOCK_PG_PROVIDER = "MOCK";
	private static final String MOCK_PAYMENT_FAILED_REASON = "mock 결제 실패";

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final PaymentRepository paymentRepository;

	@Override
	@Transactional
	public PaymentResponse create(
		UUID orderId,
		UUID userId,
		PaymentMethod paymentMethod,
		String idempotencyKey
	) {
		Preconditions.validate(
			userId != null,
			ErrorCode.AUTH_INVALID
		);

		Preconditions.validate(
			!paymentRepository.existsByIdempotencyKey(idempotencyKey),
			ErrorCode.PAYMENT_IDEMPOTENCY_KEY_ALREADY_EXISTS
		);

		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		Preconditions.validate(
			order.getUserId().equals(userId),
			ErrorCode.AUTH_PERMISSION_DENIED
		);

		Preconditions.validate(
			order.getOrderStatus() == OrderStatus.PENDING,
			ErrorCode.ORDER_PAYMENT_NOT_ALLOWED
		);

		PaymentEntity payment = PaymentEntity.create(
			order,
			null,
			PaymentType.PAYMENT,
			paymentMethod,
			order.getTotalAmount(),
			MOCK_PG_PROVIDER,
			null,
			idempotencyKey
		);

		if (shouldFail(idempotencyKey)) {
			payment.fail(MOCK_PAYMENT_FAILED_REASON);
		} else {
			payment.succeed(generateMockPgTid());
			confirmOrder(order);
		}

		paymentRepository.save(payment);

		return PaymentResponse.from(
			payment.getId(),
			payment.getOrder().getId(),
			payment.getPaymentType(),
			payment.getPaymentMethod(),
			payment.getPaymentAmount(),
			payment.getPgProvider(),
			payment.getPgTid(),
			payment.getPaymentStatus(),
			payment.getPaidAt(),
			payment.getFailedReason()
		);
	}

	private boolean shouldFail(String idempotencyKey) {
		return idempotencyKey.toLowerCase().contains("fail");
	}

	private void confirmOrder(OrderEntity order) {
		order.confirm();
		for (OrderItemEntity orderItem : orderItemRepository.findAllByOrder_Id(order.getId())) {
			orderItem.pay();
		}
	}

	private String generateMockPgTid() {
		return "mock-" + UUID.randomUUID();
	}
}
