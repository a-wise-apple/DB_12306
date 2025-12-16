package com.example.railway.web.controller;

import com.example.railway.service.TradingService;
import com.example.railway.web.dto.CreateListingRequest;
import com.example.railway.web.dto.TicketListingResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/list")
    public ResponseEntity<Void> createListing(@RequestBody CreateListingRequest request, @RequestParam Integer userId) {
        tradingService.createListing(userId, request.getOrderId(), request.getPrice());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<Void> buyListing(@PathVariable Integer id, @RequestParam Integer buyerId) {
        tradingService.buyListing(buyerId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelListing(@PathVariable Integer id, @RequestParam Integer userId) {
        tradingService.cancelListing(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TicketListingResponse>> getAllListings() {
        return ResponseEntity.ok(tradingService.findAllActiveListings());
    }
}
