package com.example.railway.web.controller;

import com.example.railway.service.CheckinService;
import com.example.railway.service.ScheduleService;
import com.example.railway.service.TradingService;
import com.example.railway.web.dto.CreateScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ScheduleService scheduleService;
    private final TradingService tradingService;
    private final CheckinService checkinService;

    @PostMapping("/schedules")
    public ResponseEntity<Void> createSchedule(@RequestBody CreateScheduleRequest request) {
        scheduleService.createSchedule(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable Integer id) {
        tradingService.adminDeleteListing(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkin")
    public ResponseEntity<Void> checkin(@RequestParam Integer ticketId, 
                                      @RequestParam Integer stationId, 
                                      @RequestParam Integer employeeId) {
        checkinService.checkin(ticketId, stationId, employeeId);
        return ResponseEntity.ok().build();
    }
}
