package com.example.railway.service;

import com.example.railway.domain.Train;
import com.example.railway.repository.TrainRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainService {
    private final TrainRepository trainRepository;

    public List<Train> findAll() {
        return trainRepository.findAll();
    }
}
