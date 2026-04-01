package com.goti.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

	private String bootstrapServers;

	private Topic topic = new Topic();
	private Consumer consumer = new Consumer();

	@Data
	public static class Topic {
		private String name;
		private int partitions;
		private int replicationFactor;
	}

	@Data
	public static class Consumer {
		private String groupId;
	}
}