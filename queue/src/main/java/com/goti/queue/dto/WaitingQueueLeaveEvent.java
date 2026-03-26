package com.goti.queue.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대기열 이탈 이벤트")
public record WaitingQueueLeaveEvent(
	UUID gameId,
	UUID userId,
	boolean isFromActive,
	Long queueNum
) {
	
}
