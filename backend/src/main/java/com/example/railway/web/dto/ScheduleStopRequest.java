package com.example.railway.web.dto;

import java.time.LocalTime;
import lombok.Data;

@Data
public class ScheduleStopRequest {
    private Integer stationId;
    private LocalTime arrivalTime;
    private LocalTime departureTime; // Not used in entity but useful for logic? Entity only has arrivalTime.
    // Wait, ScheduleStop only has arrivalTime?
    // Let's check ScheduleStop.java again.
    private Integer sequence;
}
