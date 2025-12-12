package com.example.railway.repository;

import com.example.railway.domain.Train;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Integer> {

    Optional<Train> findByTrainNo(String trainNo);
}
