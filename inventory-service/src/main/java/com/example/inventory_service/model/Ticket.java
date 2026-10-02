package com.example.inventory_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "is_reserved")
    private boolean isReserved;

    @Column(name = "reserved_by_user_id")
    private String reservedByUserId;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "stripe_payment_id")
    private String stripePaymentId;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

}
