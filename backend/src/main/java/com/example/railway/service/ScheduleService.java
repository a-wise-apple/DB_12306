package com.example.railway.service;

import com.example.railway.domain.Coach;
import com.example.railway.domain.ScheduleStop;
import com.example.railway.domain.Seat;
import com.example.railway.domain.SeatAllocation;
import com.example.railway.domain.Train;
import com.example.railway.domain.TrainSchedule;
import com.example.railway.domain.enumeration.ScheduleStatus;
import com.example.railway.domain.enumeration.SeatAllocationStatus;
import com.example.railway.repository.CoachRepository;
import com.example.railway.repository.ScheduleStopRepository;
import com.example.railway.repository.SeatAllocationRepository;
import com.example.railway.repository.SeatRepository;
import com.example.railway.repository.StationRepository;
import com.example.railway.repository.TrainRepository;
import com.example.railway.repository.TrainScheduleRepository;
import com.example.railway.web.dto.CoachRequest;
import com.example.railway.web.dto.CreateScheduleRequest;
import com.example.railway.web.dto.ScheduleStopRequest;
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
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;

    public void createSchedule(CreateScheduleRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrain(train);
        schedule.setDepartDate(request.getDepartDate());
        schedule.setStatus(ScheduleStatus.PLANNED);
        trainScheduleRepository.save(schedule);

        // Create Stops
        for (ScheduleStopRequest stopRequest : request.getStops()) {
            ScheduleStop stop = new ScheduleStop();
            stop.setSchedule(schedule);
            stop.setStation(stationRepository.findById(stopRequest.getStationId())
                    .orElseThrow(() -> new RuntimeException("Station not found")));
            stop.setSequence(stopRequest.getSequence());
            stop.setArrivalTime(stopRequest.getArrivalTime());
            stop.setDepartureTime(stopRequest.getDepartureTime());
            scheduleStopRepository.save(stop);
        }

        // Create Coaches and Seats
        for (CoachRequest coachRequest : request.getCoaches()) {
            Coach coach = new Coach();
            coach.setSchedule(schedule);
            coach.setCoachNo(coachRequest.getCoachNo());
            coach.setCoachType(coachRequest.getCoachType());
            coach.setSeatCount(coachRequest.getSeatCount());
            coachRepository.save(coach);

            for (int i = 1; i <= coachRequest.getSeatCount(); i++) {
                Seat seat = new Seat();
                seat.setCoach(coach);
                seat.setSeatNo(String.format("%d%s", i, "A")); // Simplified seat numbering
                seat.setSeatClass(coachRequest.getCoachType());
                seatRepository.save(seat);

                // Create Allocation (Initial State)
                SeatAllocation allocation = new SeatAllocation();
                allocation.setSchedule(schedule);
                allocation.setSeat(seat);
                allocation.setStatus(SeatAllocationStatus.AVAILABLE);
                seatAllocationRepository.save(allocation);
            }
        }
    }

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
