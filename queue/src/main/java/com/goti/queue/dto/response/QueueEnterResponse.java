package com.goti.queue.dto.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대기열 진입 응답 정보")
public record QueueEnterResponse(
	@Schema(description = "암호화된 보안 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
	String secureToken,

	@Schema(description = "나의 대기 순번", example = "100")
	Long myQueueNum,

	@Schema(description = "경기 ID")
	UUID gameId
) {
}
