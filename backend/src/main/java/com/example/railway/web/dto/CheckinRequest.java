package com.example.railway.web.dto;

import com.example.railway.domain.enumeration.CheckinType;
import jakarta.validation.constraints.NotNull;

public class CheckinRequest {

    @NotNull
    private Integer ticketId;

    @NotNull
    private Integer employeeId;

    @NotNull
    private Integer stationId;

    @NotNull
    private CheckinType type;

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public CheckinType getType() {
        return type;
    }

    public void setType(CheckinType type) {
        this.type = type;
    }
}
