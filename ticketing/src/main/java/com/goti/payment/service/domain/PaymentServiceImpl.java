package com.goti.payment.service.domain;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.OrderStatus;
import com.goti.constants.PaymentMethod;
import com.goti.constants.PaymentType;
import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.payment.PaymentEntity;
import com.goti.exception.CustomException;
import com.goti.global.validation.Preconditions;
import com.goti.order.repository.OrderRepository;
import com.goti.payment.dto.response.PaymentResponse;
import com.goti.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private static final String MOCK_PG_PROVIDER = "MOCK";

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;

	@Override
	@Transactional
	public PaymentResponse create(
		UUID orderId,
		PaymentMethod paymentMethod,
		String idempotencyKey
	) {
		Preconditions.validate(
			!paymentRepository.existsByIdempotencyKey(idempotencyKey),
			ErrorCode.PAYMENT_IDEMPOTENCY_KEY_ALREADY_EXISTS
		);

		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		Preconditions.validate(
			order.getOrderStatus() == OrderStatus.PENDING,
			ErrorCode.ORDER_PAYMENT_NOT_ALLOWED
		);

		PaymentEntity payment = PaymentEntity.create(
			order,
			PaymentType.PAYMENT,
			paymentMethod,
			order.getTotalAmount(),
			MOCK_PG_PROVIDER,
			null,
			idempotencyKey
		);

		PaymentEntity savedPayment = paymentRepository.save(payment);

		return PaymentResponse.from(
			savedPayment.getId(),
			savedPayment.getOrder().getId(),
			savedPayment.getPaymentType(),
			savedPayment.getPaymentMethod(),
			savedPayment.getPaymentAmount(),
			savedPayment.getPgProvider(),
			savedPayment.getPgTid(),
			savedPayment.getPaymentStatus(),
			savedPayment.getPaidAt(),
			null
		);
	}
}
