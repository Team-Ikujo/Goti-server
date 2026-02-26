package com.goti.infra.api.dto.response.common;

import java.util.Map;

public record SocialUserInfoResponse(
	String providerId,
	String name,
	String email,
	String mobile,
	String birthDate,
	String gender
) {

	public static SocialUserInfoResponse of(
		String providerId,
		String name,
		String email,
		String mobile,
		String birthDate,
		String gender
	) {
		return new SocialUserInfoResponse(
			providerId, name, email, mobile, birthDate, gender
		);
	}

	public static SocialUserInfoResponse fromNaver(Map<String, Object> response) {
		var resNode = (Map<String, Object>) response.get("response");
		return new SocialUserInfoResponse(
			(String) resNode.get("id"),
			(String) resNode.get("name"),
			(String) resNode.get("email"),
			(String) resNode.get("mobile"),
			formatDate((String) resNode.get("birthyear"), (String) resNode.get("birthday")),
			(String) resNode.get("gender")
		);
	}

	public static SocialUserInfoResponse fromKakao(Map<String, Object> response) {
		var account = (Map<String, Object>) response.get("kakao_account");
		var profile = (Map<String, Object>) account.get("profile");
		return new SocialUserInfoResponse(
			String.valueOf(response.get("id")),
			(String) profile.get("nickname"),
			(String) account.get("email"),
			(String) account.get("phone_number"),
			formatDate((String) account.get("birthyear"), (String) account.get("birthday")),
			(String) account.get("gender")
		);
	}

	public static SocialUserInfoResponse fromGoogle(Map<String, Object> response) {
		return new SocialUserInfoResponse(
			String.valueOf(response.get("sub")),
			(String) response.get("name"),
			(String) response.get("email"),
			(String) response.get("mobile"),
			(String) response.get("birthday"),
			(String) response.get("gender")
		);
	}

	private static String formatDate(String year, String day) {
		if (year == null || day == null) return null;
		return year + "-" + day.replace("-", "");
	}
}
