package com.example.railway.repository;

import com.example.railway.domain.TrainSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Integer> {

    List<TrainSchedule> findByDepartDateBetween(LocalDate start, LocalDate end);

    @Query("select ts from TrainSchedule ts join fetch ts.train where ts.train.trainNo = :trainNo and ts.departDate = :departDate")
    List<TrainSchedule> findByTrainNoAndDepartDate(@Param("trainNo") String trainNo,
                                                   @Param("departDate") LocalDate departDate);
}
