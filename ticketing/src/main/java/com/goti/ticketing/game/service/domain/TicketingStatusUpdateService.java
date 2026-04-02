package com.goti.ticketing.game.service.domain;

import com.goti.ticketing.domain.entity.game.GameScheduleEntity;

public interface TicketingStatusUpdateService {

	void processSoldout(GameScheduleEntity gameSchedule);

	void processRestoreAvailable(GameScheduleEntity gameSchedule);
}
