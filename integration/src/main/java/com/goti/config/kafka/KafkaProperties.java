package com.goti.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
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
		// 처음부터 읽을지(earliest), 최신 메시지부터 읽을지(latest) 설정
		private String autoOffsetReset = "earliest";
	}
}