package com.goti.ticketing.infra.api;

import com.goti.stadium.domain.entity.stadium.StadiumEntity;
import com.goti.stadium.domain.entity.team.BaseballTeamEntity;

import java.util.UUID;

public interface StadiumClient {
	BaseballTeamEntity getBaseballTeam(UUID teamId);
	StadiumEntity getStadium(UUID stadiumId);
}
