package com.example.railway.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TicketListingResponse {
    private Integer listingId;
    private BigDecimal price;
    private String sellerName;
    private String trainNumber;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String seatInfo; // e.g., "Coach 1, Seat 1A"
    private String status;
}
