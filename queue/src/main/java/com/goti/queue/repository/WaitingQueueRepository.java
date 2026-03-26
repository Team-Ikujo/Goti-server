package com.goti.queue.repository;

import java.util.UUID;

public interface WaitingQueueRepository {

	Long issueNextQueueNum(UUID gameId);

	void enqueue(UUID gameId, UUID userId, Long queueNumber);

	Long removeFromWaiting(UUID gameId, UUID userId);

	boolean removeFromActive(UUID gameId, UUID userId);

	Long getAllowedQueueNum(UUID gameId);

	void moveToActive(UUID gameId, UUID userId, String uuid, long ttlSeconds);

	boolean isActiveSessionExist(UUID gameId, UUID userId);

	boolean renewWaitingStatus(UUID gameId, UUID userId, long ttlSeconds);

	boolean checkDuplicateEnqueue(UUID gameId, UUID userId, long ttlSeconds);

	boolean checkDuplicateEventProcess(UUID gameId, UUID userId, long ttlSeconds);

	void incrementCurrentUsers(UUID gameId, int delta);

	void updateAllowedNum(UUID gameId, long allowedNum);

	void initializeQueueStatus(UUID gameId, long maxCapacity);

	Long getCurrentUsers(UUID gameId);

	Long getMaxCapacity(UUID gameId);

	Long getLastIssuedNum(UUID gameId);

	Long getNthQueueNum(UUID gameId, long n);
}
