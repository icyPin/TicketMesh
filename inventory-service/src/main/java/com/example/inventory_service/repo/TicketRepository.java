package com.example.inventory_service.repo;

import com.example.inventory_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findBySeatNumber(String seatNumber);

    @Query("SELECT t FROM Ticket t WHERE t.isReserved = true AND t.paymentStatus = :status AND t.reservedAt < :cutoffTime")
    List<Ticket> findExpiredReservations(

            @Param("status") String status,
            @Param("cutoffTime") LocalDateTime cutoffTime
    );

}
