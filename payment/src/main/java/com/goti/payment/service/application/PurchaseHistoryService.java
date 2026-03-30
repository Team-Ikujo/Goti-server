package com.goti.payment.service.application;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.global.validation.Preconditions;
import com.goti.payment.dto.response.PurchaseHistoryResponse;
import com.goti.payment.infra.ResaleOrderClient;
import com.goti.payment.infra.TicketingOrderClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {
	private static final String NORMAL = "NORMAL";
	private static final String RESALE = "RESALE";

	private final TicketingOrderClient ticketingOrderClient;
	private final ResaleOrderClient resaleOrderClient;

	@Transactional(readOnly = true)
	public List<PurchaseHistoryResponse> getAll(UUID memberId) {
		Preconditions.validate(
			memberId != null,
			ErrorCode.AUTH_INVALID
		);

		List<PurchaseHistoryResponse> normalOrders = ticketingOrderClient.getOrders(memberId).stream()
			.map(order -> new PurchaseHistoryResponse(
				NORMAL,
				order.orderId(),
				order.orderNumber(),
				order.orderStatus().name(),
				order.totalQuantity(),
				order.totalAmount(),
				order.orderedAt(),
				order.gameId(),
				order.stadiumId(),
				null,
				null,
				List.of()
			))
			.toList();

		List<PurchaseHistoryResponse> resaleOrders = resaleOrderClient.getPurchases(memberId).stream()
			.map(order -> new PurchaseHistoryResponse(
				RESALE,
				order.orderId(),
				order.orderNumber(),
				order.orderStatus(),
				order.totalQuantity(),
				order.totalAmount(),
				order.orderedAt(),
				order.gameId(),
				null,
				order.gameTitle(),
				order.gameDate(),
				order.seatInfos()
			))
			.toList();

		return Stream.concat(normalOrders.stream(), resaleOrders.stream())
			.sorted(Comparator.comparing(PurchaseHistoryResponse::orderedAt).reversed())
			.toList();
	}
}
