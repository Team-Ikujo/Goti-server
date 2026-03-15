package com.goti.service.application;

import java.util.UUID;

import com.goti.service.dto.PaymentOrderInfo;

import org.springframework.stereotype.Service;

import com.goti.constants.OrderStatus;
import com.goti.infra.TicketingOrderClient;
import com.goti.service.dto.OrderPaymentConfirmApiRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderGateway {
	private final TicketingOrderClient ticketingOrderClient;

	public PaymentOrderInfo getPaymentOrder(
		UUID orderId,
		UUID userId
	) {
		log.info("주문 조회 - orderId: {}, userId: {}", orderId, userId);

		// TODO: 실제 구현 시 ticketing/order 도메인 API 또는 RestClient로 조회
		return new PaymentOrderInfo(
			orderId,
			userId,
			OrderStatus.PENDING,
			1000
		);
	}

	public void confirmPayment(
		UUID orderId,
		UUID userId,
		UUID paymentId,
		String pgTid
	) {
		log.info("주문 결제 확정 - orderId: {}, userId: {}", orderId, userId);
		ticketingOrderClient.confirmPayment(
			orderId,
			new OrderPaymentConfirmApiRequest(userId, paymentId, pgTid)
		);
	}
}
