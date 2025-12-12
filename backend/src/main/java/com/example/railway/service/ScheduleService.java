package com.example.railway.service;

import com.example.railway.domain.ScheduleStop;
import com.example.railway.domain.SeatAllocation;
import com.example.railway.domain.TrainSchedule;
import com.example.railway.domain.enumeration.SeatAllocationStatus;
import com.example.railway.repository.ScheduleStopRepository;
import com.example.railway.repository.SeatAllocationRepository;
import com.example.railway.repository.TrainScheduleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final TrainScheduleRepository trainScheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;
    private final SeatAllocationRepository seatAllocationRepository;

    public List<TrainSchedule> findByDateRange(LocalDate start, LocalDate end) {
        return trainScheduleRepository.findByDepartDateBetween(start, end);
    }

    public List<TrainSchedule> findByTrainNoAndDate(String trainNo, LocalDate date) {
        return trainScheduleRepository.findByTrainNoAndDepartDate(trainNo, date);
    }

    public TrainSchedule getRequiredSchedule(Integer scheduleId) {
        return trainScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));
    }

    public List<ScheduleStop> getStops(TrainSchedule schedule) {
        return scheduleStopRepository.findByScheduleOrderBySequenceAsc(schedule);
    }

    public List<SeatAllocation> getAllocationsForSchedule(Integer scheduleId) {
        TrainSchedule schedule = getRequiredSchedule(scheduleId);
        return seatAllocationRepository.findByScheduleAndStatus(schedule, SeatAllocationStatus.AVAILABLE);
    }

    public int releaseExpiredLocks(LocalDateTime now) {
        return seatAllocationRepository.invalidateExpiredLocks(now);
    }
}
