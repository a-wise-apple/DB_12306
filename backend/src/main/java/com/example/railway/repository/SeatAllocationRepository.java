package com.example.railway.repository;

import com.example.railway.domain.BookingOrder;
import com.example.railway.domain.SeatAllocation;
import com.example.railway.domain.TrainSchedule;
import com.example.railway.domain.enumeration.SeatAllocationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Integer> {

    Optional<SeatAllocation> findByScheduleIdAndSeatId(Integer scheduleId, Integer seatId);

    List<SeatAllocation> findByScheduleAndStatus(TrainSchedule schedule, SeatAllocationStatus status);

    List<SeatAllocation> findByReservedByOrder(BookingOrder order);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SeatAllocation sa set sa.status = :status, sa.reservedByOrder = null, sa.reservedUntil = null where sa.reservedByOrder = :order")
    int releaseOrderAllocations(@Param("order") BookingOrder order,
                                @Param("status") SeatAllocationStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update SeatAllocation sa set sa.status = 'AVAILABLE', sa.reservedByOrder = null, sa.reservedUntil = null where sa.status = 'LOCKED' and sa.reservedUntil < :now")
    int invalidateExpiredLocks(@Param("now") LocalDateTime now);
}
