package com.goti.resale.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResaleTicketPurchaseInfoResponse(
	UUID ticketId,
	String gameTitle,
	LocalDateTime gameDate,
	String seatInfo
) {
}
