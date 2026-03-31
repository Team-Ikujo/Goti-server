package com.goti.payment.service.application;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.global.validation.Preconditions;
import com.goti.payment.dto.request.enums.PurchaseHistoryType;
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
	public List<PurchaseHistoryResponse> getAll(
		UUID memberId,
		PurchaseHistoryType type,
		Integer months,
		LocalDate startDate,
		LocalDate endDate
	) {
		Preconditions.validate(
			memberId != null,
			ErrorCode.AUTH_INVALID
		);

		PurchaseHistoryType purchaseHistoryType = type != null ? type : PurchaseHistoryType.ALL;

		List<PurchaseHistoryResponse> normalOrders = getNormalOrders(
			memberId,
			purchaseHistoryType,
			months,
			startDate,
			endDate
		);

		List<PurchaseHistoryResponse> resaleOrders = getResaleOrders(
			memberId,
			purchaseHistoryType,
			months,
			startDate,
			endDate
		);

		return Stream.concat(normalOrders.stream(), resaleOrders.stream())
			.sorted(Comparator.comparing(PurchaseHistoryResponse::orderedAt).reversed())
			.toList();
	}

	private List<PurchaseHistoryResponse> getNormalOrders(
		UUID memberId,
		PurchaseHistoryType type,
		Integer months,
		LocalDate startDate,
		LocalDate endDate
	) {
		if (type == PurchaseHistoryType.RESALE) {
			return List.of();
		}

		return ticketingOrderClient.getOrders(memberId, months, startDate, endDate).stream()
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
	}

	private List<PurchaseHistoryResponse> getResaleOrders(
		UUID memberId,
		PurchaseHistoryType type,
		Integer months,
		LocalDate startDate,
		LocalDate endDate
	) {
		if (type == PurchaseHistoryType.NORMAL) {
			return List.of();
		}

		return resaleOrderClient.getPurchases(memberId, months, startDate, endDate).stream()
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
	}
}
