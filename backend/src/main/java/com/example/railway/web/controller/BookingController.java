package com.example.railway.web.controller;

import com.example.railway.domain.BookingOrder;
import com.example.railway.domain.UserAccount;
import com.example.railway.service.BookingService;
import com.example.railway.service.UserService;
import com.example.railway.web.dto.ReserveSeatsRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingOrder>> getUserBookings(@PathVariable("userId") Integer userId) {
        // In real app, validate current user matches userId
        return userService.findById(userId)
            .map(user -> ResponseEntity.ok(bookingService.findByUser(user)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    // We need findById in UserService
    
    @PostMapping("/reserve")
    public ResponseEntity<BookingOrder> reserveSeats(@Valid @RequestBody ReserveSeatsRequest request) {
        UserAccount user = userService.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BookingOrder order = new BookingOrder();
        order.setUser(user);
        order.setTotalAmount(java.math.BigDecimal.ZERO);
        
        return ResponseEntity.ok(bookingService.createOrder(order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable("id") Integer id) {
        bookingService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
