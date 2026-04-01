package com.goti.kafka;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.Getter;

// 테스트 실행 시 사용할 가짜 yml 설정값 주입
@SpringBootTest(properties = {
	"spring.kafka.bootstrap-servers=localhost:9092",
	"spring.kafka.topic.name=test-local-topic",
	"spring.kafka.consumer.group-id=test-test-group"
})
@Import(KafkaConfigurationTest.TestConsumer.class)
public class KafkaConfigurationTest {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private TestConsumer testConsumer;

	@Test
	@DisplayName("Kafka 설정이 정상 동작하여 메시지를 보내고 받을 수 있다")
	void testKafkaSendAndReceive() throws InterruptedException {
		// given
		String testMessage = "Hello, MSA Kafka Test!";

		// when: 메시지 발행
		kafkaTemplate.send("test-local-topic", testMessage);

		// then: 메시지 수신 대기 (비동기 통신이므로 최대 5초간 기다림)
		boolean messageConsumed = testConsumer.getLatch().await(5, TimeUnit.SECONDS);

		// 검증: 5초 안에 메시지를 받았는가? 내용이 일치하는가?
		assertThat(messageConsumed).isTrue();
		assertThat(testConsumer.getReceivedPayload()).isEqualTo(testMessage);
	}

	// =========================================================
	// 테스트용 내부 클래스들 (실제 운영 코드에는 영향을 주지 않음)
	// =========================================================

	// 1. 설정들을 스캔해서 스프링 컨테이너를 띄워줄 가짜 메인 앱
	@SpringBootApplication(scanBasePackages = "com.goti.kafka.config")
	static class TestApplication {
	}

	// 2. 메시지가 잘 오는지 확인할 가짜 컨슈머 (수신자)
	@Getter
	@Component
	static class TestConsumer {
		// 비동기 처리 완료를 기다리기 위한 자물쇠 (1번 수신하면 잠금 해제)
		private final CountDownLatch latch = new CountDownLatch(1);
		private String receivedPayload;

		@KafkaListener(
			topics = "${spring.kafka.topic.name}",
			groupId = "${spring.kafka.consumer.group-id}"
		)
		public void receive(String message) {
			this.receivedPayload = message;
			this.latch.countDown(); // 메시지를 받으면 자물쇠 카운트 감소
		}

	}
}