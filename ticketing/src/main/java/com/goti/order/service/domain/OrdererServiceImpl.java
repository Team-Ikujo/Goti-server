package com.goti.order.service.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goti.domain.entity.order.OrderEntity;
import com.goti.domain.entity.order.OrdererEntity;
import com.goti.order.repository.OrdererRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdererServiceImpl implements OrdererService {
	private final OrdererRepository ordererRepository;

	@Override
	@Transactional
	public OrdererEntity create(
		OrderEntity order,
		String name,
		String phone,
		String email
	) {
		OrdererEntity orderer = OrdererEntity.create(order, name, phone, email);
		return ordererRepository.save(orderer);
	}
}
