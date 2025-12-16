package com.example.railway.web.dto;

import lombok.Data;

@Data
public class CoachRequest {
    private String coachNo;
    private String coachType; // e.g., "First Class", "Second Class"
    private Integer seatCount;
}
