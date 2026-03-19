package com.propertyservice.Repository;

import com.propertyservice.Entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area,Long> {

    Area findByName(String name);
}
