package com.goti.queue.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "queue.policy")
public record QueueProperties(
	long waitingTtl,
	long activeTtl,
	long leaveEventTtl,
	long enqueueDuplicateTtl
) {
}
