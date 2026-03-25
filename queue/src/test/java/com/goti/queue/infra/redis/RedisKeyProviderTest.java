package com.goti.queue.infra.redis;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class RedisKeyProviderTest {

	final RedisKeyProvider keyProvider = new RedisKeyProvider();

	@Test
	void 만료된_대기열_하트비트_키_파싱() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String key = "queue:waiting:heartbeat:" + gameId + ":" + userId;

		RedisKeyProvider.ExpiredKeyInfo info = keyProvider.parseExpiredKey(key);

		assertThat(info).isNotNull();
		assertThat(info.gameId()).isEqualTo(gameId);
		assertThat(info.userId()).isEqualTo(userId);
		assertThat(info.isActive()).isFalse();
	}

	@Test
	void 만료된_활성_세션_키_파싱() {
		UUID gameId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String key = "queue:active:" + gameId + ":" + userId;

		RedisKeyProvider.ExpiredKeyInfo info = keyProvider.parseExpiredKey(key);

		assertThat(info).isNotNull();
		assertThat(info.gameId()).isEqualTo(gameId);
		assertThat(info.userId()).isEqualTo(userId);
		assertThat(info.isActive()).isTrue();
	}

	@Test
	void 유효하지_않은_키_파싱() {
		assertThat(keyProvider.parseExpiredKey(null)).isNull();
		assertThat(keyProvider.parseExpiredKey("invalid:key:format")).isNull();
		assertThat(keyProvider.parseExpiredKey("queue:status:some-game-id")).isNull();
	}
}
