package com.propertyservice.Repository;

import com.propertyservice.Entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property,Long> {
    @Query("""
    SELECT DISTINCT p
    FROM Property p
    JOIN p.city c
    JOIN p.area a
    JOIN p.state s
    JOIN Rooms r ON r.property = p
    JOIN RoomAvailability ra ON ra.room = r
    WHERE (
        LOWER(c.name) LIKE LOWER(CONCAT('%', :location, '%')) OR
        LOWER(a.name) LIKE LOWER(CONCAT('%', :location, '%')) OR
        LOWER(s.name) LIKE LOWER(CONCAT('%', :location, '%'))
    )
    AND ra.availableDate = :date
    AND ra.availableCount > 0
""")
    List<Property> searchAvailableProperties(
            @Param("location") String location,
            @Param("date") LocalDate date
    );
}
