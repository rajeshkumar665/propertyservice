package com.propertyservice.Repository;

import com.propertyservice.Entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability,Long> {

    List<RoomAvailability> findByRoomId(long id);
    // ✅ NEW - Decrement availableCount safely (won't go below 0)
    @Modifying
    @Query("UPDATE RoomAvailability r " +
            "SET r.availableCount = r.availableCount - 1 " +
            "WHERE r.id = :roomAvailabilityId " +
            "AND r.availableDate = :date " +
            "AND r.availableCount > 0")
    int decrementAvailability(
            @Param("roomAvailabilityId") long roomAvailabilityId,
            @Param("date") LocalDate date
    );

}