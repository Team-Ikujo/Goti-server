package com.goti.payment.service.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.global.validation.Preconditions;
import com.goti.payment.dto.response.PaymentResponse;
import com.goti.payment.service.domain.PaymentService;
import com.goti.payment.service.dto.PaymentOrderInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {
	private final PaymentOrderGateway paymentOrderGateway;
	private final PaymentService paymentService;

	@Transactional(readOnly = true)
	public PaymentResponse getByOrderId(UUID orderId, UUID memberId) {
		Preconditions.validate(
			memberId != null,
			ErrorCode.AUTH_INVALID
		);

		PaymentOrderInfo order = paymentOrderGateway.getPaymentOrder(orderId, memberId);
		Preconditions.validate(
			order.memberId().equals(memberId),
			ErrorCode.AUTH_PERMISSION_DENIED
		);

		return paymentService.getByOrderId(orderId);
	}
}
