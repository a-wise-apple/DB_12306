package com.example.railway.repository;

import com.example.railway.domain.ScheduleStop;
import com.example.railway.domain.TrainSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleStopRepository extends JpaRepository<ScheduleStop, Integer> {

    List<ScheduleStop> findByScheduleOrderBySequenceAsc(TrainSchedule schedule);
}
