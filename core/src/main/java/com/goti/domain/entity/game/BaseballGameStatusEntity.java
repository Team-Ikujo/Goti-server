package com.goti.domain.entity.game;

import com.goti.constants.GameResult;
import com.goti.constants.GameStatus;
import com.goti.domain.base.ModificationTimestampEntity;

import com.goti.global.validation.Preconditions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.*;

@Getter
@Entity
@Table(name = "baseball_game_statuses")
@NoArgsConstructor(access = PROTECTED)
public class BaseballGameStatusEntity extends ModificationTimestampEntity {

	@Column(name = "baseball_games_id", nullable = false)
	private UUID baseballGameId;

	@Enumerated(EnumType.STRING)
	@Column(name = "game_status", nullable = false, length = 50)
	private GameStatus gameStatus;

	@Column(name = "home_team_score", nullable = false)
	private Integer homeTeamScore;

	@Column(name = "away_team_score", nullable = false)
	private Integer awayTeamScore;

	@Enumerated(EnumType.STRING)
	@Column(name = "game_result", length = 50)
	private GameResult gameResult;

	private BaseballGameStatusEntity(
		UUID baseballGameId,
		GameStatus gameStatus,
		Integer homeTeamScore,
		Integer awayTeamScore,
		GameResult gameResult
	) {
		this.baseballGameId = baseballGameId;
		this.gameStatus = gameStatus;
		this.homeTeamScore = homeTeamScore;
		this.awayTeamScore = awayTeamScore;
		this.gameResult = gameResult;
	}

	public static BaseballGameStatusEntity create(
		UUID baseballGameId,
		GameStatus gameStatus,
		Integer homeTeamScore,
		Integer awayTeamScore,
		GameResult gameResult
	) {

		validate(
			baseballGameId,
			gameStatus,
			homeTeamScore,
			awayTeamScore
		);

		return new BaseballGameStatusEntity(
			baseballGameId,
			gameStatus == null ? GameStatus.SCHEDULED : gameStatus,
			homeTeamScore == null ? 0 : homeTeamScore,
			awayTeamScore == null ? 0 : awayTeamScore,
			gameResult
		);
	}

	private static void validate(
		UUID baseballGameId,
		GameStatus gameStatus,
		Integer homeTeamScore,
		Integer awayTeamScore
	) {

		Preconditions.domainValidate(
			baseballGameId != null,
			"경기 ID는 필수입니다."
		);
		Preconditions.domainValidate(
			gameStatus != null,
			"경기 상태는 필수입니다."
		);

		Preconditions.domainValidate(
			homeTeamScore != null && homeTeamScore >= 0,
			"홈팀 점수는 0 이상이어야 합니다."
		);

		Preconditions.domainValidate(
			awayTeamScore != null && awayTeamScore >= 0,
			"원정팀 점수는 0 이상이어야 합니다."
		);
	}
}
