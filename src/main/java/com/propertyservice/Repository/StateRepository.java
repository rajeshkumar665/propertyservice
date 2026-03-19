package com.propertyservice.Repository;

import com.propertyservice.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
    State findByName(String name);
}
