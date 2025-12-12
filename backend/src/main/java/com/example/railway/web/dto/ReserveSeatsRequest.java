package com.example.railway.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ReserveSeatsRequest {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer scheduleId;

    @NotEmpty
    private List<SeatPassengerPayload> seats;

    @Min(1)
    private Integer holdMinutes = 15;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public List<SeatPassengerPayload> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatPassengerPayload> seats) {
        this.seats = seats;
    }

    public Integer getHoldMinutes() {
        return holdMinutes;
    }

    public void setHoldMinutes(Integer holdMinutes) {
        this.holdMinutes = holdMinutes;
    }

    public static class SeatPassengerPayload {

        @NotNull
        private Integer seatId;

        @NotNull
        private String passengerName;

        public Integer getSeatId() {
            return seatId;
        }

        public void setSeatId(Integer seatId) {
            this.seatId = seatId;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName(String passengerName) {
            this.passengerName = passengerName;
        }
    }
}
