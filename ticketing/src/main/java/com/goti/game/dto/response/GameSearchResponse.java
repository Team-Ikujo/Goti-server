package com.goti.game.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.goti.constants.LeagueType;

import com.goti.constants.TicketingStatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "게임 일정 조회 응답")
public record GameSearchResponse(
	UUID gameScheduleId,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime startAt,
	String homeTeamName,
	String awayTeamName,
	String stadiumName,
	LeagueType leagueType,
	TicketingStatus ticketingStatus,
	String gameStatus
) {
}
