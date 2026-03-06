package com.goti.seat.service.command;

import java.util.UUID;

public record HoldSeatCommand(
	UUID gameId,
	UUID seatId,
	UUID userId,
	String queueTokenJti
) {
}
