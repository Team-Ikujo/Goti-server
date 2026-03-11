package com.goti.order.service.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.order.OrderHistoryEntity;
import com.goti.order.repository.OrdererRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdererServiceImpl implements OrdererService {
	private final OrdererRepository ordererRepository;

	@Override
	@Transactional
	public OrderHistoryEntity create(
		OrderEntity order,
		String name,
		String phone,
		String email
	) {
		OrderHistoryEntity orderer = OrderHistoryEntity.create(order, name, phone, email);
		return ordererRepository.save(orderer);
	}
}
