package com.example.inventory_service.service;


import com.example.inventory_service.model.Ticket;
import com.example.inventory_service.repo.TicketRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    TicketRepository ticketRepository;
    StringRedisTemplate redisTemplate;

    public InventoryService(TicketRepository ticketRepository , StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
        this.ticketRepository=ticketRepository;
    }

    public boolean reserveSeat(String eventName , String seatNumber , String userId){
        String lockKey = "lock:seat:" + eventName + ":" + seatNumber;

        try{
            Boolean aquiredLock = redisTemplate.opsForValue().setIfAbsent(
                    lockKey , "locked" , Duration.ofSeconds(10));
            if(!aquiredLock){
                System.out.println("current seat already locked ,  number"+ seatNumber);
                return false;
            }
        }catch(Exception e){
            System.out.println("server could not be reached due to internal error");
            return false;
        }

        try{
            //I can add event-name as param too but i am assuming seat no. is unique
            //bcs I am too tired to do otherwise.

            Ticket ticket = ticketRepository.findBySeatNumber(seatNumber);

            if(ticket==null || ticket.isReserved()){
                System.out.println("this seat is sold out or not available");
                return false;
            }

            ticket.setReserved(true);
            ticket.setPaymentStatus("PROCESSING");
            ticket.setReservedByUserId(userId);
            ticket.setReservedAt(LocalDateTime.now());
            ticketRepository.save(ticket);

            System.out.println("seat with id = "+ticket.getId()+" confirmed...yehhh!!");
            return true;
        }finally {
            redisTemplate.delete(lockKey);
        }
    }

    public String confirmPayment(Long id){
        return ticketRepository.findById(id).map(ticket -> {
            ticket.setPaymentStatus("PAID");
            ticketRepository.save(ticket);
            return "payment successful";
        }).orElseGet(()-> "");
    }

    @Scheduled(fixedRate = 60000)
    public void releaseSeat(){
        LocalDateTime delTime = LocalDateTime.now().minusMinutes(10);

        List<Ticket> expTickets = ticketRepository.findExpiredReservations("PROCESSING" , delTime);

        if(!expTickets.isEmpty()){
            for(Ticket ticket: expTickets){
                ticket.setReserved(false);
                ticket.setReservedByUserId(null);
                ticket.setPaymentStatus("PENDING");
                System.out.println("changed "+ ticket.getId() +"to pending");
                ticket.setStripePaymentId(null);
                ticket.setReservedAt(null);
            }
            ticketRepository.saveAll(expTickets);
        }
    }
}
