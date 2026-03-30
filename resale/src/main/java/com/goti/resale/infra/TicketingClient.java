package com.goti.resale.infra;

import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.global.api.ApiSuccessResponse;
import com.goti.infra.api.base.BaseRestClient;
import com.goti.resale.config.properties.TicketingApiProperties;
import com.goti.resale.dto.response.ResaleTicketPurchaseInfoResponse;

@Component
public class TicketingClient extends BaseRestClient {
	private static final String TICKET_API = "/api/v1/tickets";

	public TicketingClient(RestClient.Builder builder, TicketingApiProperties properties) {
		super(builder, properties.baseUrl());
	}

	public ResaleTicketPurchaseInfoResponse getPurchaseInfo(UUID ticketId) {
		String uri = String.format("%s/%s/purchase-info", TICKET_API, ticketId);
		var response = getGotiResponse(
			uri,
			null,
			null,
			new ParameterizedTypeReference<ApiSuccessResponse<ResaleTicketPurchaseInfoResponse>>() {}
		);

		if (response == null) {
			throw new CustomException(ErrorCode.INTERNAL_API_INVALID_RESPONSE);
		}

		return response;
	}
}
