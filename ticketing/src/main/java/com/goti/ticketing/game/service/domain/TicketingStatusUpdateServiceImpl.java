package com.goti.ticketing.game.service.domain;

import com.goti.ticketing.constants.SeatStatus;
import com.goti.ticketing.constants.TicketingStatus;
import com.goti.ticketing.domain.entity.game.GameScheduleEntity;
import com.goti.ticketing.domain.entity.game.GameTicketingStatusEntity;
import com.goti.ticketing.game.repository.GameTicketingStatusRepository;
import com.goti.ticketing.seat.repository.SeatStatusRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketingStatusUpdateServiceImpl implements TicketingStatusUpdateService {
	private final GameTicketingStatusRepository gameTicketingStatusRepository;
	private final SeatStatusRepository seatStatusRepository;

	@Override
	public void processSoldout(GameScheduleEntity gameSchedule) {
		GameTicketingStatusEntity ticketingStatus = getLockedTicketingStatus(gameSchedule);

		if (ticketingStatus == null || ticketingStatus.isExhausted()) {
			return;
		}

		if (countRemainingSeats(gameSchedule) == 0) {
			ticketingStatus.updateStatus(TicketingStatus.EXHAUSTED);
		}
	}

	@Override
	public void processRestoreAvailable(GameScheduleEntity gameSchedule) {
		GameTicketingStatusEntity ticketingStatus = getLockedTicketingStatus(gameSchedule);

		if (ticketingStatus == null || !ticketingStatus.isExhausted()) {
			return;
		}

		if (countRemainingSeats(gameSchedule) > 0) {
			ticketingStatus.updateStatus(TicketingStatus.AVAILABLE);
		}
	}

	private GameTicketingStatusEntity getLockedTicketingStatus(GameScheduleEntity gameSchedule) {
		return gameTicketingStatusRepository.findByGameSchedule(gameSchedule)
			.orElse(null);
	}

	private long countRemainingSeats(GameScheduleEntity gameSchedule) {
		return seatStatusRepository.countByGameAndStatus(gameSchedule, SeatStatus.AVAILABLE);
	}
}
