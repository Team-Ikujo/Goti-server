package com.goti.order.service.command;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
	UUID gameId,
	UUID memberId,
	List<UUID> holdIds,
	String ordererName,
	String ordererPhone,
	String ordererEmail
) {
}
