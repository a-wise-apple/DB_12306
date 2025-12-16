package com.example.railway.service;

import com.example.railway.domain.Checkin;
import com.example.railway.domain.Employee;
import com.example.railway.domain.Station;
import com.example.railway.domain.Ticket;
import com.example.railway.domain.enumeration.CheckinType;
import com.example.railway.domain.enumeration.TicketStatus;
import com.example.railway.repository.CheckinRepository;
import com.example.railway.repository.EmployeeRepository;
import com.example.railway.repository.StationRepository;
import com.example.railway.repository.TicketRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final TicketRepository ticketRepository;
    private final StationRepository stationRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void checkin(Integer ticketId, Integer stationId, Integer employeeId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new RuntimeException("Ticket is not active");
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Create Checkin Record
        Checkin checkin = new Checkin();
        checkin.setTicket(ticket);
        checkin.setStation(station);
        checkin.setEmployee(employee);
        // checkinTime is handled by DB
        checkin.setType(CheckinType.ENTRY); 
        checkinRepository.save(checkin);

        // Update Ticket Status
        ticket.setStatus(TicketStatus.CHECKED_IN);
        ticketRepository.save(ticket);
    }
}
