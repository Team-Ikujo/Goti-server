package com.goti.ticketing.session.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.goti.infra.cache.RedisCache;
import com.goti.infra.constants.redis.RedisKey;
import com.goti.ticketing.session.model.ReservationSessionCache;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationSessionRedisRepository {
	private static final String KEY_DELIMITER = ":";

	private final RedisCache redisCache;

	public void save(
		UUID memberId,
		UUID gameId,
		ReservationSessionCache reservationSession
	) {
		redisCache.set(
			RedisKey.RESERVATION_SESSION,
			generateKeyParam(memberId, gameId),
			reservationSession
		);
	}

	public Optional<ReservationSessionCache> find(
		UUID memberId,
		UUID gameId
	) {
		return Optional.ofNullable(
			redisCache.get(
				RedisKey.RESERVATION_SESSION.getKey(generateKeyParam(memberId, gameId)),
				ReservationSessionCache.class
			)
		);
	}

	public boolean delete(
		UUID memberId,
		UUID gameId
	) {
		return redisCache.delete(
			RedisKey.RESERVATION_SESSION.getKey(generateKeyParam(memberId, gameId))
		);
	}

	private String generateKeyParam(UUID memberId, UUID gameId) {
		return memberId + KEY_DELIMITER + gameId;
	}
}
