package com.example.railway.service;

import com.example.railway.domain.BookingOrder;
import com.example.railway.domain.UserAccount;
import com.example.railway.domain.enumeration.OrderStatus;
import com.example.railway.repository.BookingOrderRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingOrderRepository bookingOrderRepository;

    public List<BookingOrder> findByUser(UserAccount user) {
        return bookingOrderRepository.findByUser(user);
    }

    public BookingOrder createOrder(BookingOrder order) {
        order.setStatus(OrderStatus.PENDING);
        return bookingOrderRepository.save(order);
    }
    
    public BookingOrder findById(Integer id) {
        return bookingOrderRepository.findById(id).orElse(null);
    }

    public void cancelOrder(Integer orderId) {
        BookingOrder order = bookingOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        bookingOrderRepository.save(order);
        
        // TODO: Release seat allocations associated with this order
        // For now, we assume SeatAllocation logic is handled separately or we need to inject SeatAllocationRepository
    }
}
