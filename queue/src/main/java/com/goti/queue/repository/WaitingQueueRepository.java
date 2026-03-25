package com.goti.queue.repository;

import java.util.UUID;

public interface WaitingQueueRepository {

	Long issueNextQueueNum(UUID gameId);

	void enqueue(UUID gameId, UUID userId, Long queueNumber);

	Long removeFromWaiting(UUID gameId, UUID userId);

	boolean removeFromActive(UUID gameId, UUID userId);

	Long getAllowedQueueNum(UUID gameId);

	void moveToActive(UUID gameId, UUID userId, String uuid, long ttlSeconds);

	boolean renewWaitingStatus(UUID gameId, UUID userId, long ttlSeconds);

	boolean renewActiveStatus(UUID gameId, UUID userId, long ttlSeconds);

	boolean checkDuplicateEnqueue(UUID gameId, UUID userId, long ttlSeconds);

	boolean checkDuplicateEventProcess(UUID gameId, UUID userId, long ttlSeconds);

	void incrementCurrentUsers(UUID gameId, int delta);

	void incrementAllowedNum(UUID gameId, int delta);

	void initializeQueueStatus(UUID gameId, long maxCapacity);

	Long getCurrentUsers(UUID gameId);
}
