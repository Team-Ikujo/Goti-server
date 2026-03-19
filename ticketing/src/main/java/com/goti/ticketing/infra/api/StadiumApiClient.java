package com.goti.ticketing.infra.api;

import com.goti.config.properties.ApiEndpointProperties;
import com.goti.infra.api.base.BaseRestClient;

import com.goti.stadium.domain.entity.stadium.StadiumEntity;
import com.goti.stadium.domain.entity.team.BaseballTeamEntity;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class StadiumApiClient extends BaseRestClient implements StadiumClient {

	private final static String BASEBALL_TEAM_GET_API = "/api/v1/baseball-teams/";
	private final static String STADIUM_GET_API = "/api/v1/stadiums/";

	public StadiumApiClient(RestClient.Builder builder, ApiEndpointProperties properties) {
		super(builder, properties.stadium());
	}

	@Override
	public BaseballTeamEntity getBaseballTeam(UUID teamId) {
		String uri = BASEBALL_TEAM_GET_API + teamId;
		return get(uri, null, null, BaseballTeamEntity.class);
	}

	@Override
	public StadiumEntity getStadium(UUID stadiumId) {
		String uri = STADIUM_GET_API + stadiumId;
		return get(uri, null, null, StadiumEntity.class);
	}
}
