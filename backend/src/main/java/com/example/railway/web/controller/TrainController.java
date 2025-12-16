package com.example.railway.web.controller;

import com.example.railway.domain.Train;
import com.example.railway.service.TrainService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.findAll();
    }
}
