package com.example.railway.repository;

import com.example.railway.domain.Station;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {

    Optional<Station> findByCode(String code);
}
