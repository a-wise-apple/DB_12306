package com.example.railway.repository;

import com.example.railway.domain.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

    List<Seat> findByCoachId(Integer coachId);
}
