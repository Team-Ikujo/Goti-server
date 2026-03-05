package com.goti.seat.utils;

import com.goti.seat.service.application.SeatHoldExpiryApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
	prefix = "seat.hold-expiry",
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
public class SeatHoldExpiryScheduler {
	private final SeatHoldExpiryApplicationService seatHoldExpiryApplicationService;

	@Value("${seat.hold-expiry.batch-size}")
	private int batchSize;

	@Scheduled(fixedDelayString = "${seat.hold-expiry.fixed-delay-ms}")
	public void expireHolds() {
		int processed = seatHoldExpiryApplicationService.expireHolds(batchSize);

		if (processed > 0) {
			log.info("좌석 점유 만료 처리 완료. processed={}", processed);
		}
	}
}
