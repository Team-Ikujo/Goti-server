package com.goti.domain.entity.ticket;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.goti.constants.TicketIssueStatus;
import com.goti.constants.TicketResaleStatus;
import com.goti.domain.base.ModificationTimestampEntity;
import com.goti.domain.entity.order.OrderItemEntity;
import com.goti.global.validation.Preconditions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "tickets",
	indexes = {
		@Index(name = "idx_tickets_game_id", columnList = "game_id"),
		@Index(name = "idx_tickets_user_id", columnList = "user_id"),
		@Index(name = "idx_tickets_ticket_status", columnList = "ticket_status")
	}
)
@NoArgsConstructor(access = PROTECTED)
public class TicketEntity extends ModificationTimestampEntity {

	@Column(nullable = false, unique = true)
	private String ticketNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id", nullable = false, unique = true)
	private OrderItemEntity orderItem;

	@Column(nullable = false)
	private UUID gameId;

	@Column(nullable = false)
	private UUID userId;

	private String userNickname;

	private String userEmail;

	private String userPhone;

	private String gameTitle;

	private LocalDateTime gameDate;

	@Column(nullable = false)
	private String seatInfo;

	@Column(nullable = false)
	private Integer ticketPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketIssueStatus ticketStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketResaleStatus ticketResaleStatus;

	@Column(unique = true)
	private String qrCode;

	private LocalDateTime usedAt;

	private TicketEntity(
		String ticketNumber,
		OrderItemEntity orderItem,
		UUID gameId,
		UUID userId,
		String userNickname,
		String userEmail,
		String userPhone,
		String gameTitle,
		LocalDateTime gameDate,
		String seatInfo,
		Integer ticketPrice,
		String qrCode
	) {
		this.ticketNumber = ticketNumber;
		this.orderItem = orderItem;
		this.gameId = gameId;
		this.userId = userId;
		this.userNickname = userNickname;
		this.userEmail = userEmail;
		this.userPhone = userPhone;
		this.gameTitle = gameTitle;
		this.gameDate = gameDate;
		this.seatInfo = seatInfo;
		this.ticketPrice = ticketPrice;
		this.ticketStatus = TicketIssueStatus.ISSUED;
		this.ticketResaleStatus = TicketResaleStatus.DISABLED;
		this.qrCode = qrCode;
		this.usedAt = null;
	}

	public static TicketEntity create(
		String ticketNumber,
		OrderItemEntity orderItem,
		UUID gameId,
		UUID userId,
		String userNickname,
		String userEmail,
		String userPhone,
		String gameTitle,
		LocalDateTime gameDate,
		String seatInfo,
		Integer ticketPrice,
		String qrCode
	) {
		validate(
			ticketNumber,
			gameId,
			userId,
			seatInfo,
			ticketPrice
		);
		return new TicketEntity(
			ticketNumber,
			orderItem,
			gameId,
			userId,
			userNickname,
			userEmail,
			userPhone,
			gameTitle,
			gameDate,
			seatInfo,
			ticketPrice,
			qrCode
		);
	}

	private static void validate(
		String ticketNumber,
		UUID gameId,
		UUID userId,
		String seatInfo,
		Integer ticketPrice
	) {
		Preconditions.domainValidate(
			StringUtils.hasText(ticketNumber),
			"티켓 번호는 비어 있을 수 없습니다."
		);
		Preconditions.domainValidate(
			gameId != null,
			"경기 ID는 필수입니다."
		);
		Preconditions.domainValidate(
			userId != null,
			"유저 ID는 필수입니다."
		);
		Preconditions.domainValidate(
			StringUtils.hasText(seatInfo),
			"좌석 정보는 비어 있을 수 없습니다."
		);
		Preconditions.domainValidate(
			ticketPrice != null && ticketPrice > 0,
			"티켓 가격은 0원보다 커야합니다."
		);
	}
}
