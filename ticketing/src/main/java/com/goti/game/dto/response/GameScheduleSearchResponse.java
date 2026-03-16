package com.goti.game.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.goti.constants.LeagueType;

import com.goti.constants.TicketingStatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "야구 경기 일정 상세 정보 응답")
public record GameScheduleSearchResponse(
	@Schema(description = "게임 식별자 ID", example = "880e8400-e29b-41d4-a716-446655440000")
	UUID gameId,

	@Schema(description = "경기 시작 일시", example = "2026-03-20 18:30")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime startAt,

	@Schema(description = "홈 팀 명칭", example = "KIA 타이거즈")
	String homeTeamName,

	@Schema(description = "어웨이 팀 명칭", example = "삼성 라이온즈")
	String awayTeamName,

	@Schema(description = "경기장 명칭", example = "광주-기아 챔피언스 필드")
	String stadiumName,

	@Schema(description = "리그 유형", example = "REGULAR")
	LeagueType leagueType,

	@Schema(description = "티켓 구매 가능 상태", example = "AVAILABLE")
	TicketingStatus ticketingStatus,

	@Schema(description = "경기 진행 상태", example = "STARTED")
	String gameStatus
) {}
