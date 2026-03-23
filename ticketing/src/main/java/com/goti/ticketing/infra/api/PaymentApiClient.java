package com.goti.ticketing.infra.api;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.goti.config.properties.ApiEndpointProperties;
import com.goti.infra.api.base.BaseRestClient;
import com.goti.ticketing.infra.api.dto.PaymentCancelResponse;
import com.goti.ticketing.order.dto.request.OrderPaymentCancelRequest;

@Component
public class PaymentApiClient extends BaseRestClient {

	public PaymentApiClient(RestClient.Builder builder, ApiEndpointProperties properties) {
		super(builder, properties.payment());
	}

	public PaymentCancelResponse.PaymentResponseData cancelPayment(UUID orderId, UUID cancellationId) {
		PaymentCancelResponse response = post(
			"/api/v1/payments/orders/" + orderId + "/cancellations",
			new OrderPaymentCancelRequest(cancellationId),
			PaymentCancelResponse.class
		);
		return response.data();
	}
}
