package com.goti.infra.api.dto.response.cloudflare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TurnstileVerifyResponse(
	boolean success,
	@JsonProperty("error-codes") List<String> errorCodes
) {
}
