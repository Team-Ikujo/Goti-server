package game;

import com.goti.constants.LeagueType;
import com.goti.domain.entity.game.GameScheduleEntity;

import com.goti.domain.entity.game.GameTicketingStatusEntity;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
public class GameTicketingStatusEntityTest {

	GameScheduleEntity gameSchedule;

	static final LocalDateTime START_AT = LocalDateTime.now().plusDays(3);
	static final LeagueType LEAGUE_TYPE = LeagueType.REGULAR;
	static final LocalDateTime OPENED_AT = LocalDateTime.now().plusDays(3);
	static final LocalDateTime CLOSED_AT = LocalDateTime.now().plusDays(5);

	@BeforeEach
	void setup() {
		gameSchedule = GameScheduleEntity.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			START_AT,
			LEAGUE_TYPE
		);
	}

	@Test
	void 게임_티켓팅_상태_생성_성공() {
		GameTicketingStatusEntity gameTicketingStatus = GameTicketingStatusEntity.create(
			gameSchedule, OPENED_AT, CLOSED_AT
		);

		assertNotNull(gameTicketingStatus);
		log.info("gameTicketingStatus :: {}", gameTicketingStatus);
	}
}
