package com.goti.seat.utils;

import com.goti.infra.lock.DistributedLockManager;
import com.goti.seat.service.application.SeatHoldExpiryApplicationService;
import com.goti.seat.service.application.SeatHoldExpiryBatchResult;
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
	private static final String EXPIRY_JOB_LOCK_KEY = "lock:seat-expiry-job";

	private final SeatHoldExpiryApplicationService seatHoldExpiryApplicationService;
	private final DistributedLockManager distributedLockManager;

	@Value("${seat.hold-expiry.batch-size}")
	private int batchSize;

	@Scheduled(fixedDelayString = "${seat.hold-expiry.fixed-delay-ms}")
	public void expireHolds() {
		boolean acquired = distributedLockManager.withLockIfAvailable(
			EXPIRY_JOB_LOCK_KEY,
			() -> {
				SeatHoldExpiryBatchResult result = seatHoldExpiryApplicationService.expireHolds(batchSize);

				if (result.attempted() > 0) {
					log.info(
						"좌석 점유 만료 처리 완료. attempted={}, succeeded={}, failed={}",
						result.attempted(),
						result.succeeded(),
						result.failed()
					);
				}
			}
		);

		if (!acquired) {
			log.debug("좌석 만료 스케줄러 락을 획득하지 못해 이번 실행을 건너뜁니다.");
		}
	}
}
