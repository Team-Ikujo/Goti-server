package com.goti.ticketing.ticket.service.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.ticketing.constants.TicketFreezeReason;
import com.goti.ticketing.domain.entity.ticket.TicketEntity;
import com.goti.ticketing.domain.entity.ticket.TicketFreezeEntity;
import com.goti.ticketing.ticket.service.domain.TicketFreezeService;
import com.goti.ticketing.ticket.service.domain.TicketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketFreezeManageService {
	private final TicketService ticketService;
	private final TicketFreezeService ticketFreezeService;

	@Transactional
	public TicketFreezeEntity freezeTicket(
		UUID ticketId,
		TicketFreezeReason freezeReason
	) {
		TicketEntity ticket = ticketService.get(ticketId);
		return ticketFreezeService.freezeTicket(ticket, freezeReason);
	}

	@Transactional(readOnly = true)
	public boolean isFrozen(UUID ticketId) {
		return ticketFreezeService.isFrozen(ticketId);
	}

	@Transactional(readOnly = true)
	public TicketFreezeEntity getActiveFreeze(UUID ticketId) {
		return ticketFreezeService.getActiveFreeze(ticketId);
	}
}
