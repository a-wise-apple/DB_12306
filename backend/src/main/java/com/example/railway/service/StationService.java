package com.example.railway.service;

import com.example.railway.domain.Station;
import com.example.railway.repository.StationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StationService {

    private final StationRepository stationRepository;

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station create(Station station) {
        return stationRepository.save(station);
    }
}
