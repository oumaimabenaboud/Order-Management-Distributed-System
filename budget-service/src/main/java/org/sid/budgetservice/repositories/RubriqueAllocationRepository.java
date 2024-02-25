package org.sid.budgetservice.repositories;

import org.sid.budgetservice.entities.RubriqueAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RubriqueAllocationRepository extends JpaRepository<RubriqueAllocation, Long> {
    // Additional custom methods can be defined here
}
