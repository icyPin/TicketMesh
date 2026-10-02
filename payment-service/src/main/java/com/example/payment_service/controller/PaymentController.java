package com.example.payment_service.controller;

import com.example.payment_service.dto.PaymentResponseDto;
import com.example.payment_service.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private PaymentService paymentService;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @PostMapping("/create-intent/{ticketId}")
    public ResponseEntity<PaymentResponseDto> createIntent(@PathVariable Long ticketId) throws StripeException {
        try{
            String secret = paymentService.paymentIntent(ticketId);
            return ResponseEntity.ok(new PaymentResponseDto(secret));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(new PaymentResponseDto(null, e.getMessage()));
        }

    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader){

        System.out.println("Webhook ping received!");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("SIGNATURE FAILED:");
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            System.out.println("OTHER ERROR: " + e.getMessage());
            return ResponseEntity.status(500).body("Error");
        }

        System.out.println("Signature verified! Event type: " + event.getType());

        if("payment_intent.succeeded".equals(event.getType())){
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            if(dataObjectDeserializer.getObject().isPresent()){
                PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();

                String ticketID =  paymentIntent.getMetadata().get("ticketId");
                if(ticketID!=null){
                    Long id = Long.parseLong(ticketID);

                    // inventory call to update the status to "paid" >> will add it later
                    RestTemplate template = new RestTemplate();
                    String inventoryUrl = "http://localhost:8082/api/inventory/tickets/" + ticketID + "/confirm-payment";
                    try{
                        template.postForEntity(inventoryUrl,null,String.class);
                    }catch (Exception e){
                        System.out.println("failed to lock payment");
                    }
                    System.out.println("payment of ticket id: "+ id+ " is completed"  );
                }
            }
        }

        return ResponseEntity.ok("Successes");
    }
}
