package com.goti.seat.service.command;

import java.util.UUID;

public record ReleaseSeatCommand(
	UUID holdId,
	UUID userId
) {
}
