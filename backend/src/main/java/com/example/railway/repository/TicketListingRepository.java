package com.example.railway.repository;

import com.example.railway.domain.TicketListing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketListingRepository extends JpaRepository<TicketListing, Integer> {
    List<TicketListing> findByStatus(String status);
}
