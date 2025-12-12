package com.example.railway.repository;

import com.example.railway.domain.BookingOrder;
import com.example.railway.domain.UserAccount;
import com.example.railway.domain.enumeration.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, Integer> {

    List<BookingOrder> findByUser(UserAccount user);

    List<BookingOrder> findByStatus(OrderStatus status);
}
