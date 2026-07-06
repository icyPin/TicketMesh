package com.example.inventory_service.repo;

import com.example.inventory_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findBySeatNumber(String seatNumber);

}
