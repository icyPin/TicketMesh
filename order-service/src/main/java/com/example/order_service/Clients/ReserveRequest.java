package com.example.order_service.Clients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveRequest {
    private String eventName;
    private String seatNumber;
    private String userId;

}