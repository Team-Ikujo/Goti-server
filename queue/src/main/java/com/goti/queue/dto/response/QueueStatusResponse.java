package com.goti.queue.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대기열 상태 응답 정보")
public record QueueStatusResponse(
	@Schema(description = "나의 대기 순번", example = "100")
	Long myQueueNum,
	@Schema(description = "현재 입장이 허용된 마지막 순번", example = "50")
	Long allowedQueueNum,
	@Schema(description = "내 앞의 대기 인원 수", example = "49")
	Long waitingCount,
	@Schema(description = "입장 가능 여부", example = "false")
	boolean isAllowed
) {
}
