package com.example.inventory_service.controller;


import com.example.inventory_service.service.InventoryService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

@Data
class ReserveRequest{
    private String eventName;
    private String seatNumber;
    private String userId;
}