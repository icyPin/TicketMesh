package com.example.inventory_service.service;


import com.example.inventory_service.model.Ticket;
import com.example.inventory_service.repo.TicketRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

        Boolean aquiredLock = redisTemplate.opsForValue().setIfAbsent(
                lockKey , "locked" , Duration.ofSeconds(10));

        try{
            if(!aquiredLock){
                System.out.println("current seat number"+ seatNumber+" could not be locked");
                return false;
            }
        }catch(Exception e){
            System.out.println("server could not be reached due to internal error");
            return false;
        }

        try{
            Ticket ticket = ticketRepository.findBySeatNumber(seatNumber);

            if(ticket==null){
                ticket= new Ticket();
                ticket.setEventName(eventName);
                ticket.setSeatNumber(seatNumber);
                ticket.setReserved(false);
            }

            if(ticket.isReserved()){
                System.out.println("this seat is sold out");
                return false;
            }

            ticket.setReserved(true);
            ticket.setReservedByUserId(userId);
            ticketRepository.save(ticket);

            System.out.println("seat with id = "+seatNumber+" confirmed...yehhh!!");
            return true;
        }finally {
            redisTemplate.delete(lockKey);
        }
    }

}
