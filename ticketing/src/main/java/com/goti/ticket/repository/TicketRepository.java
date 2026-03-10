package com.goti.ticket.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goti.domain.entity.ticket.TicketEntity;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
}
