package com.goti.domain.entity.baseball;

import com.goti.domain.base.ModificationTimestampEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@Entity
@Table(
	name = "baseball_winner_history",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_winner_history_team_year", columnNames = {"baseball_team_id", "winning_year"})
	})
@NoArgsConstructor(access = PROTECTED)
public class BaseballWinnerHistoryEntity extends ModificationTimestampEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "baseball_team_id", nullable = false)
	private BaseballTeamEntity baseballTeam;

	@Column(nullable = false)
	private Integer winningYear;

	private BaseballWinnerHistoryEntity(
		BaseballTeamEntity baseballTeam,
		Integer winningYear
	) {
		this.baseballTeam = baseballTeam;
		this.winningYear = winningYear;
	}
	public static BaseballWinnerHistoryEntity create(
		BaseballTeamEntity baseballTeam,
		Integer winningYear
	) {
		return new BaseballWinnerHistoryEntity(baseballTeam, winningYear);
	}
}
