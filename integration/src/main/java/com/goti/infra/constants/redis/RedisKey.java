package com.goti.infra.constants.redis;

import java.time.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKey {
	SMS_AUTH_CODE("auth:sms:", Duration.ofMinutes(3)),
	OAUTH_STATE("oauth:state:", Duration.ofMinutes(5)),
	TICKET_QR("ticket:qr-token:", Duration.ofMinutes(3)),
	REFRESH_TOKEN("auth:refresh-token:", Duration.ofDays(7)),

	QUEUE_STATUS("queue:status:", null),
	QUEUE_WAITING("queue:waiting:", null),
	QUEUE_WAITING_HEARTBEAT("queue:waiting:heartbeat:", Duration.ofSeconds(30)),
	QUEUE_ACTIVE("queue:active:", Duration.ofMinutes(30)),
	QUEUE_PROCESS_ENQUEUE("queue:process:enqueue:", Duration.ofSeconds(15)),
	QUEUE_PROCESS_LEAVE("queue:process:leave_event:", Duration.ofSeconds(60)),
	QUEUE_LOCK_GAME("queue:lock:game:", Duration.ofSeconds(10));

	private final String prefix;

	private final Duration ttl;

	public String getKey(Object val) {
		return prefix + val;
	}

	public String getKey(Object... vals) {
		StringBuilder sb = new StringBuilder(prefix);
		for (int i = 0; i < vals.length; i++) {
			sb.append(vals[i]);
			if (i < vals.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}
}
