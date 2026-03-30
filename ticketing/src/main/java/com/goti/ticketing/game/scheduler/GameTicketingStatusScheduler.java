package com.goti.ticketing.game.scheduler;

import com.goti.ticketing.constants.TicketingStatus;
import com.goti.ticketing.domain.entity.game.GameTicketingStatusEntity;
import com.goti.ticketing.game.repository.GameTicketingStatusRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameTicketingStatusScheduler {


	private final GameTicketingStatusRepository ticketingStatusRepository;

	@Transactional
	@Scheduled(cron = "0 0 11 * * *")
	public void updateTicketingStatus() {
		List<GameTicketingStatusEntity> list = ticketingStatusRepository.findOpenableSchedules(
			TicketingStatus.SCHEDULED,
			LocalDateTime.now()
		);

		list.forEach(
			ticketingStatus -> ticketingStatus.updateStatus(TicketingStatus.AVAILABLE)
		);

	}


}
