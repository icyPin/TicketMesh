package com.example.inventory_service.controller;


import com.example.inventory_service.service.InventoryService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService){
        this.inventoryService=inventoryService;
    }
    

    @PostMapping("/reserve")
    public ResponseEntity<Boolean> reserveSeat(@RequestBody ReserveRequest request){
        System.out.println("request recieved to lock seat "+ request.getSeatNumber() + " from userID " + request.getUserId());

        boolean sucesses = inventoryService.reserveSeat(
                request.getEventName(),
                request.getSeatNumber(),
                request.getUserId()
        );

        if(sucesses){
            return ResponseEntity.ok(true);
        }
        else
            return ResponseEntity.status(409).body(false);
    }

    @PostMapping("/tickets/{ticketId}/confirm-payment")
    public ResponseEntity<String> confirm(@PathVariable Long ticketId){
        String message = inventoryService.confirmPayment(ticketId);
        if(!message.isEmpty()){
            return ResponseEntity.ok(message);
        }
        else
            return ResponseEntity.status(404).body("Ticket not found");
    }

}

@Data
class ReserveRequest{
    private String eventName;
    private String seatNumber;
    private String userId;
}