package com.example.railway.web.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class CreateScheduleRequest {
    private Integer trainId;
    private LocalDate departDate;
    private List<ScheduleStopRequest> stops;
    private List<CoachRequest> coaches;
}
