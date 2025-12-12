package com.example.railway.repository;

import com.example.railway.domain.Coach;
import com.example.railway.domain.TrainSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Integer> {

    List<Coach> findBySchedule(TrainSchedule schedule);
}
