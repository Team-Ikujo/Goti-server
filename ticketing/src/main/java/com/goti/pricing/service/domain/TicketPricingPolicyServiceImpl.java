package com.goti.pricing.service.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.constants.messages.ErrorCode;
import com.goti.domain.entity.pricing.TicketPriceEntity;
import com.goti.domain.entity.pricing.TicketPricingPolicyEntity;
import com.goti.domain.entity.seat.SeatGradeEntity;
import com.goti.global.validation.Preconditions;
import com.goti.pricing.dto.response.TicketPricingPolicyResponse;
import com.goti.pricing.repository.TicketPriceRepository;
import com.goti.pricing.repository.TicketPricingPolicyRepository;
import com.goti.seat.repository.SeatGradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketPricingPolicyServiceImpl implements TicketPricingPolicyService {
	private final TicketPricingPolicyRepository ticketPricingPolicyRepository;
	private final TicketPriceRepository ticketPriceRepository;
	private final SeatGradeRepository seatGradeRepository;

	@Override
	@Transactional
	public TicketPricingPolicyResponse create(
		UUID teamId,
		LocalDate policyStartAt,
		LocalDate policyEndAt,
		List<TicketPriceCreateParam> ticketPriceRequest,
		UUID createdBy
	) {
		TicketPricingPolicyEntity policy = TicketPricingPolicyEntity.create(
			teamId,
			policyStartAt,
			policyEndAt,
			createdBy
		);

		ticketPricingPolicyRepository.save(policy);

		Map<UUID, SeatGradeEntity> gradesById = getGrades(ticketPriceRequest);
		validateDuplicateticketPrice(policy, ticketPriceRequest);

		List<TicketPriceEntity> ticketPrices = ticketPriceRequest.stream()
			.map(price -> TicketPriceEntity.create(
				gradesById.get(price.gradeId()),
				policy,
				price.ticketType(),
				price.dayType(),
				price.matchType(),
				price.price(),
				createdBy
			))
			.toList();

		ticketPriceRepository.saveAll(ticketPrices);
		return TicketPricingPolicyResponse.from(policy, ticketPrices);
	}

	private Map<UUID, SeatGradeEntity> getGrades(List<TicketPriceCreateParam> ticketPriceRequest) {
		List<UUID> gradeIds = ticketPriceRequest.stream()
			.map(TicketPriceCreateParam::gradeId)
			.distinct()
			.toList();

		List<SeatGradeEntity> grades = seatGradeRepository.findAllById(gradeIds);

		Map<UUID, SeatGradeEntity> gradesById = new HashMap<>();
		for (SeatGradeEntity grade : grades) {
			gradesById.put(grade.getId(), grade);
		}

		for (UUID gradeId : gradeIds) {
			Preconditions.validate(
				gradesById.containsKey(gradeId),
				ErrorCode.SEAT_GRADE_NOT_FOUND
			);
		}

		return gradesById;
	}

	private void validateDuplicateticketPrice(
		TicketPricingPolicyEntity policy,
		List<TicketPriceCreateParam> ticketPriceRequest
	) {
		for (TicketPriceCreateParam price : ticketPriceRequest) {
			Preconditions.validate(
				!ticketPriceRepository.existsDuplicateTicketPrice(
					policy,
					price.gradeId(),
					price.dayType(),
					price.matchType()
				),
				ErrorCode.TICKET_PRICE_CONDITION_ALREADY_EXISTS
			);
		}
	}
}
