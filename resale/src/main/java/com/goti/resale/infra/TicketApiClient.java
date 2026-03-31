package com.goti.resale.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.goti.resale.dto.response.ResaleTicketResponse;

import com.goti.resale.infra.dto.TicketGameInfo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.global.api.ApiSuccessResponse;
import com.goti.infra.api.base.BaseRestClient;
import com.goti.resale.config.properties.TicketingApiProperties;
import com.goti.resale.infra.dto.ResaleTicketPurchaseInfo;

@Component
public class TicketApiClient extends BaseRestClient implements TicketClient {
	private static final String TICKET_API = "/api/v1/tickets";

	public TicketApiClient(RestClient.Builder builder, TicketingApiProperties properties) {
		super(builder, properties.baseUrl());
	}

	@Override
	public ResaleTicketPurchaseInfo getPurchaseInfo(UUID ticketId) {
		String uri = String.format("%s/%s/purchase-info", TICKET_API, ticketId);
		var response = getGotiResponse(
			uri,
			null,
			null,
			new ParameterizedTypeReference<ApiSuccessResponse<ResaleTicketPurchaseInfo>>() {}
		);

		if (response == null) {
			throw new CustomException(ErrorCode.INTERNAL_API_INVALID_RESPONSE);
		}

		return response;
	}

	@Override
	public ResaleTicketResponse getTicketInfo(UUID ticketId, UUID ownerId) {
		return null;
	}

	@Override
	public int getOwnedTicketCount(UUID userId, UUID gameId) {
		return 0;
	}

	@Override
	public List<UUID> getExpiredGameIds(LocalDateTime thresholdTime) {
		return null;
	}

	@Override
	public List<TicketGameInfo> getUpcomingGames() {
		return null;
	}

	@Override
	public void transferOwnership(UUID ticketId, UUID buyerId) {}
}
