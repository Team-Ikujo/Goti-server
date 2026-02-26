package com.goti.infra.api.client;

import com.goti.constants.OAuthProvider;

public interface SocialApiClient {
	OAuthProvider getProviderType();

	String getAccessToken(String code, String state);

	String getProviderId(String accessToken);
}
