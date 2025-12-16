package com.example.railway.service;

import com.example.railway.domain.BookingOrder;
import com.example.railway.domain.Ticket;
import com.example.railway.domain.TicketListing;
import com.example.railway.domain.TrainSchedule;
import com.example.railway.domain.UserAccount;
import com.example.railway.domain.enumeration.OrderStatus;
import com.example.railway.repository.BookingOrderRepository;
import com.example.railway.repository.TicketListingRepository;
import com.example.railway.repository.TicketRepository;
import com.example.railway.repository.UserAccountRepository;
import com.example.railway.web.dto.TicketListingResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradingService {

    private final TicketListingRepository ticketListingRepository;
    private final BookingOrderRepository bookingOrderRepository;
    private final UserAccountRepository userAccountRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public void createListing(Integer userId, Integer orderId, BigDecimal price) {
        BookingOrder order = bookingOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only list your own orders");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Only paid orders can be listed");
        }

        // Check if already listed
        // This is a simplification, ideally we should check if there is an active listing for this order
        
        TicketListing listing = new TicketListing();
        listing.setSeller(order.getUser());
        listing.setBookingOrder(order);
        listing.setPrice(price);
        listing.setStatus("ACTIVE");
        
        ticketListingRepository.save(listing);
    }

    @Transactional
    public void cancelListing(Integer userId, Integer listingId) {
        TicketListing listing = ticketListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own listings");
        }

        if (!"ACTIVE".equals(listing.getStatus())) {
            throw new RuntimeException("Listing is not active");
        }

        listing.setStatus("CANCELLED");
        ticketListingRepository.save(listing);
    }

    @Transactional
    public void buyListing(Integer buyerId, Integer listingId) {
        TicketListing listing = ticketListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!"ACTIVE".equals(listing.getStatus())) {
            throw new RuntimeException("Listing is not active");
        }

        if (listing.getSeller().getId().equals(buyerId)) {
            throw new RuntimeException("You cannot buy your own listing");
        }

        UserAccount buyer = userAccountRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // 1. Update Listing Status
        listing.setStatus("SOLD");
        ticketListingRepository.save(listing);

        // 2. Transfer Ownership of the Order
        BookingOrder order = listing.getBookingOrder();
        order.setUser(buyer);
        bookingOrderRepository.save(order);

        // 3. Update Passenger Name on Tickets (Optional but good for UX)
        List<Ticket> tickets = ticketRepository.findByOrderId(order.getId());
        for (Ticket ticket : tickets) {
            ticket.setPassengerName(buyer.getName());
            ticketRepository.save(ticket);
        }
    }

    @Transactional
    public void adminDeleteListing(Integer listingId) {
        TicketListing listing = ticketListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        listing.setStatus("DELETED_BY_ADMIN");
        ticketListingRepository.save(listing);
    }

    public List<TicketListingResponse> findAllActiveListings() {
        return ticketListingRepository.findByStatus("ACTIVE").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TicketListingResponse mapToResponse(TicketListing listing) {
        TicketListingResponse response = new TicketListingResponse();
        response.setListingId(listing.getId());
        response.setPrice(listing.getPrice());
        response.setSellerName(listing.getSeller().getName());
        response.setStatus(listing.getStatus());

        BookingOrder order = listing.getBookingOrder();
        // Assuming one ticket per order for simplicity in display, or taking the first one
        List<Ticket> tickets = ticketRepository.findByOrderId(order.getId());
        if (!tickets.isEmpty()) {
            Ticket ticket = tickets.get(0);
            TrainSchedule schedule = ticket.getSchedule();
            
            response.setTrainNumber(schedule.getTrain().getTrainNo());
            response.setDepartureTime(schedule.getDepartDate().atStartOfDay()); // Approximate
            // response.setDepartureStation(schedule.getRoute().getStartStation().getName()); 
            
            response.setSeatInfo(ticket.getSeat().getCoach().getCoachNo() + " - " + ticket.getSeat().getSeatNo());
        }
        return response;
    }
}
