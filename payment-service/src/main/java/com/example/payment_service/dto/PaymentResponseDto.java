package com.example.payment_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {

    private String clientSecret;
    private String error;

    public PaymentResponseDto(String clientSecret){
        this.clientSecret=clientSecret;
        this.error=null;
    }
}
