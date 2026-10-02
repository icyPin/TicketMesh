package com.example.order_service;


import com.example.order_service.model.ReserveRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderWorker {
    
    private final StringRedisTemplate redisTemplate;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OrderWorker(StringRedisTemplate redisTemplate,ObjectMapper objectMapper){
        this.redisTemplate=redisTemplate;
        this.restClient=RestClient.create();
        this.objectMapper=objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    public void processesWaitingRoom(){

        String redisPayload = redisTemplate.opsForList().rightPop("waiting-room-queue");

        if(redisPayload!=null){

            String userId="";
            String eventName="";
            String seatNumber="";
            try{
                ReserveRequest request = objectMapper.readValue(redisPayload , ReserveRequest.class);
                userId=request.getUserId();
                eventName=request.getEventName();
                seatNumber=request.getSeatNumber();

                System.out.println(
                        "pulled user with id: " + userId +
                                " Event Name: "+ eventName +
                                " Requested Seat: "+ seatNumber +
                                " out of the queue. Starting Processes..."
                );

                System.out.println("Contacting Inventory Service to lock a seat...");

                Boolean success = restClient.post()
                        .uri("http://localhost:8082/api/inventory/reserve")
                        .body(request)
                        .retrieve()
                        .body(Boolean.class);

                if (Boolean.TRUE.equals(success)) {
                    System.out.println(" SUCCESS! Booked Seat :"+seatNumber +
                            "Of Event:"+ eventName +
                            "for User: "+ userId
                    );
                } else {
                    System.out.println(" failed! The selected seat is already locked.");
                }
                System.out.println("Contacting Payment Service...");
                // boolean paymentSuccess = paymentClient.processPayment(userId);
                System.out.println(
                        "Order Complete of user with id: " + userId +
                                " Event Name: "+ eventName +
                                " Confirmed Seat: "+ seatNumber +
                                "Enjoy the event !!"
                );
            }catch (Exception e){
                System.out.println("Order failed for user of user id "+ userId + " dropping processes"+ e.getMessage());
            }
        }
    }
}
