package com.example.railway.web.controller;

import com.example.railway.domain.Station;
import com.example.railway.service.StationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping
    public List<Station> getAllStations() {
        return stationService.findAll();
    }

    @PostMapping
    public Station createStation(@RequestBody Station station) {
        return stationService.create(station);
    }
}
