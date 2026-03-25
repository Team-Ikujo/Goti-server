package com.goti.queue.infra.redis;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.infra.constants.redis.RedisKey;

@Component
public class RedisKeyProvider {

	public static final String PREFIX_WAITING_HEARTBEAT = "queue:waiting:heartbeat:";
	public static final String PREFIX_ACTIVE = "queue:active:";

	public record ExpiredKeyInfo(UUID gameId, UUID userId, boolean isActive) {
	}

	public String getStatusKey(UUID gameId) {
		return RedisKey.QUEUE_STATUS.getKey(gameId);
	}

	public String getWaitingKey(UUID gameId) {
		return RedisKey.QUEUE_WAITING.getKey(gameId);
	}

	public String getWaitingHeartbeatKey(UUID gameId, UUID userId) {
		return RedisKey.QUEUE_WAITING_HEARTBEAT.getKey(gameId, userId);
	}

	public String getActiveKey(UUID gameId, UUID userId) {
		return RedisKey.QUEUE_ACTIVE.getKey(gameId, userId);
	}

	public ExpiredKeyInfo parseExpiredKey(String key) {
		if (key == null) {
			return null;
		}

		if (key.startsWith(PREFIX_WAITING_HEARTBEAT)) {
			return parseIds(key, PREFIX_WAITING_HEARTBEAT, false);
		}

		if (key.startsWith(PREFIX_ACTIVE)) {
			return parseIds(key, PREFIX_ACTIVE, true);
		}

		return null;
	}

	private ExpiredKeyInfo parseIds(String key, String prefix, boolean isActive) {
		try {
			String data = key.substring(prefix.length());
			String[] parts = data.split(":");
			if (parts.length == 2) {
				return new ExpiredKeyInfo(
					UUID.fromString(parts[0]),
					UUID.fromString(parts[1]),
					isActive
				);
			}
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 지정 필요
		}
		return null;
	}

	public String getEnqueueProcessKey(UUID gameId, UUID userId) {
		return RedisKey.QUEUE_PROCESS_ENQUEUE.getKey(gameId, userId);
	}

	public String getLeaveEventProcessKey(UUID gameId, UUID userId) {
		return RedisKey.QUEUE_PROCESS_LEAVE.getKey(gameId, userId);
	}

	public String getLockKey(UUID gameId) {
		return RedisKey.QUEUE_LOCK_GAME.getKey(gameId);
	}
}
