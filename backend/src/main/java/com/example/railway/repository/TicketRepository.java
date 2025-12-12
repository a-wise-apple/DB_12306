package com.example.railway.repository;

import com.example.railway.domain.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByOrderId(Integer orderId);
}
