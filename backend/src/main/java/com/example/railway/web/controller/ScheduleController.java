package com.example.railway.web.controller;

import com.example.railway.domain.ScheduleStop;
import com.example.railway.domain.SeatAllocation;
import com.example.railway.domain.TrainSchedule;
import com.example.railway.service.ScheduleService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public List<TrainSchedule> getSchedules(
            @RequestParam(required = false) String trainNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (trainNo != null) {
            return scheduleService.findByTrainNoAndDate(trainNo, date);
        } else if (endDate != null) {
            return scheduleService.findByDateRange(date, endDate);
        } else {
            // Default to single day range if no end date provided
            return scheduleService.findByDateRange(date, date);
        }
    }

    @GetMapping("/{id}")
    public TrainSchedule getSchedule(@PathVariable Integer id) {
        return scheduleService.getRequiredSchedule(id);
    }

    @GetMapping("/{id}/stops")
    public List<ScheduleStop> getScheduleStops(@PathVariable Integer id) {
        TrainSchedule schedule = scheduleService.getRequiredSchedule(id);
        return scheduleService.getStops(schedule);
    }

    @GetMapping("/{id}/seats")
    public List<SeatAllocation> getSeatAllocations(@PathVariable Integer id) {
        return scheduleService.getAllocationsForSchedule(id);
    }
}
