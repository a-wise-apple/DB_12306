package com.example.railway.web.dto;

import com.example.railway.domain.enumeration.ScheduleStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleDetailResponse {

    private Integer scheduleId;
    private String trainNo;
    private String trainType;
    private LocalDate departDate;
    private ScheduleStatus status;
    private List<StopInfo> stops;
    private List<SeatAvailability> seats;

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public LocalDate getDepartDate() {
        return departDate;
    }

    public void setDepartDate(LocalDate departDate) {
        this.departDate = departDate;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public List<StopInfo> getStops() {
        return stops;
    }

    public void setStops(List<StopInfo> stops) {
        this.stops = stops;
    }

    public List<SeatAvailability> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatAvailability> seats) {
        this.seats = seats;
    }

    public static class StopInfo {
        private Integer stationId;
        private String stationName;
        private Integer sequence;
        private LocalTime arrivalTime;
        private LocalTime departureTime;

        public Integer getStationId() {
            return stationId;
        }

        public void setStationId(Integer stationId) {
            this.stationId = stationId;
        }

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }

        public LocalTime getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(LocalTime arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public LocalTime getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(LocalTime departureTime) {
            this.departureTime = departureTime;
        }
    }

    public static class SeatAvailability {
        private Integer seatId;
        private String coachNo;
        private String seatNo;
        private String seatClass;
        private String status;

        public Integer getSeatId() {
            return seatId;
        }

        public void setSeatId(Integer seatId) {
            this.seatId = seatId;
        }

        public String getCoachNo() {
            return coachNo;
        }

        public void setCoachNo(String coachNo) {
            this.coachNo = coachNo;
        }

        public String getSeatNo() {
            return seatNo;
        }

        public void setSeatNo(String seatNo) {
            this.seatNo = seatNo;
        }

        public String getSeatClass() {
            return seatClass;
        }

        public void setSeatClass(String seatClass) {
            this.seatClass = seatClass;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
