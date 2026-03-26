package com.goti.queue.infra.redis;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.queue.repository.WaitingQueueRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRedisRepository implements WaitingQueueRepository {

	private final StringRedisTemplate redisTemplate;
	private final RedisKeyProvider keyProvider;

	private static final String FIELD_LAST_ISSUED_NUM = "lastIssuedNum";
	private static final String FIELD_ALLOWED_NUM = "allowedNum";
	private static final String FIELD_CURRENT_USERS = "currentUsers";
	private static final String FIELD_MAX_CAPACITY = "maxCapacity";

	@Override
	public Long issueNextQueueNum(UUID gameId) {
		validateNotNull(gameId, "gameId");
		return redisTemplate.opsForHash().increment(keyProvider.getStatusKey(gameId), FIELD_LAST_ISSUED_NUM, 1);
	}

	@Override
	public void enqueue(UUID gameId, UUID userId, Long queueNumber) {
		validateNotNull(gameId, "gameId");
		validateNotNull(userId, "userId");
		validateNotNull(queueNumber, "queueNumber");

		redisTemplate.opsForZSet().add(keyProvider.getWaitingKey(gameId), userId.toString(), queueNumber);
	}

	@Override
	public Long removeFromWaiting(UUID gameId, UUID userId) {
		validateNotNull(gameId, "gameId");
		validateNotNull(userId, "userId");

		String waitingKey = keyProvider.getWaitingKey(gameId);
		Double score = redisTemplate.opsForZSet().score(waitingKey, userId.toString());

		redisTemplate.delete(keyProvider.getWaitingHeartbeatKey(gameId, userId));
		redisTemplate.opsForZSet().remove(waitingKey, userId.toString());

		return score != null ? score.longValue() : null;
	}

	@Override
	public boolean removeFromActive(UUID gameId, UUID userId) {
		validateNotNull(gameId, "gameId");
		validateNotNull(userId, "userId");

		return BooleanUtils.isTrue(redisTemplate.delete(keyProvider.getActiveKey(gameId, userId)));
	}

	@Override
	public Long getAllowedQueueNum(UUID gameId) {
		validateNotNull(gameId, "gameId");

		Object allowedNum = redisTemplate.opsForHash().get(keyProvider.getStatusKey(gameId), FIELD_ALLOWED_NUM);
		return Optional.ofNullable(allowedNum)
			.map(obj -> Long.parseLong(obj.toString()))
			.orElse(0L);
	}

	@Override
	public void moveToActive(UUID gameId, UUID userId, String uuid, long ttlSeconds) {
		validateNotNull(gameId, "gameId");
		validateNotNull(userId, "userId");
		validateNotBlank(uuid, "uuid");

		redisTemplate.opsForValue().set(keyProvider.getActiveKey(gameId, userId), uuid, ttlSeconds, TimeUnit.SECONDS);
	}

	@Override
	public boolean isActiveSessionExist(UUID gameId, UUID userId) {
		validateNotNull(gameId, "gameId");
		validateNotNull(userId, "userId");

		return BooleanUtils.isTrue(redisTemplate.hasKey(keyProvider.getActiveKey(gameId, userId)));
	}

	@Override
	public boolean renewWaitingStatus(UUID gameId, UUID userId, long ttlSeconds) {
		validateNotNull(gameId, "gameId");
		validateNotNull(userId, "userId");

		String key = keyProvider.getWaitingHeartbeatKey(gameId, userId);
		redisTemplate.opsForValue().set(key, "stay", ttlSeconds, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public boolean checkDuplicateEnqueue(UUID gameId, UUID userId, long ttlSeconds) {
		String key = keyProvider.getEnqueueProcessKey(gameId, userId);
		return BooleanUtils.isTrue(redisTemplate.opsForValue().setIfAbsent(key, "1", ttlSeconds, TimeUnit.SECONDS));
	}

	@Override
	public boolean checkDuplicateEventProcess(UUID gameId, UUID userId, long ttlSeconds) {
		String key = keyProvider.getLeaveEventProcessKey(gameId, userId);
		return BooleanUtils.isTrue(redisTemplate.opsForValue().setIfAbsent(key, "1", ttlSeconds, TimeUnit.SECONDS));
	}

	@Override
	public void incrementCurrentUsers(UUID gameId, int delta) {
		redisTemplate.opsForHash().increment(keyProvider.getStatusKey(gameId), FIELD_CURRENT_USERS, delta);
	}
	
	@Override
	public void updateAllowedNum(UUID gameId, long allowedNum) {
		redisTemplate.opsForHash().put(keyProvider.getStatusKey(gameId), FIELD_ALLOWED_NUM, String.valueOf(allowedNum));
	}

	@Override
	public void initializeQueueStatus(UUID gameId, long maxCapacity) {
		String statusKey = keyProvider.getStatusKey(gameId);
		redisTemplate.opsForHash().put(statusKey, FIELD_ALLOWED_NUM, String.valueOf(maxCapacity));
		redisTemplate.opsForHash().put(statusKey, FIELD_MAX_CAPACITY, String.valueOf(maxCapacity));
		redisTemplate.opsForHash().put(statusKey, FIELD_CURRENT_USERS, "0");
		redisTemplate.opsForHash().put(statusKey, FIELD_LAST_ISSUED_NUM, "0");
	}

	@Override
	public Long getCurrentUsers(UUID gameId) {
		Object currentUsers = redisTemplate.opsForHash()
			.get(keyProvider.getStatusKey(gameId), FIELD_CURRENT_USERS);
		return Optional.ofNullable(currentUsers)
			.map(obj -> Long.parseLong(obj.toString()))
			.orElse(0L);
	}

	@Override
	public Long getMaxCapacity(UUID gameId) {
		Object maxCapacity = redisTemplate.opsForHash()
			.get(keyProvider.getStatusKey(gameId), FIELD_MAX_CAPACITY);
		return Optional.ofNullable(maxCapacity)
			.map(obj -> Long.parseLong(obj.toString()))
			.orElse(0L);
	}

	@Override
	public Long getLastIssuedNum(UUID gameId) {
		Object lastIssuedNum = redisTemplate.opsForHash()
			.get(keyProvider.getStatusKey(gameId), FIELD_LAST_ISSUED_NUM);
		return Optional.ofNullable(lastIssuedNum)
			.map(obj -> Long.parseLong(obj.toString()))
			.orElse(0L);
	}

	@Override
	public Long getNthQueueNum(UUID gameId, long n) {
		if (n <= 0)
			return null;
		String key = keyProvider.getWaitingKey(gameId);
		var range = redisTemplate.opsForZSet().rangeWithScores(key, n - 1, n - 1);
		if (range == null || range.isEmpty())
			return null;
		Double score = range.iterator().next().getScore();
		return score != null ? score.longValue() : null;
	}

	private void validateNotBlank(String value, String paramName) {
		if (value == null || value.isBlank()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER, paramName);
		}
	}

	private void validateNotNull(Object value, String paramName) {
		if (value == null) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER, paramName);
		}
	}
}
