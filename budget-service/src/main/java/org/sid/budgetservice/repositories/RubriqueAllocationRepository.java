package org.sid.budgetservice.repositories;

import org.sid.budgetservice.entities.RubriqueAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RubriqueAllocationRepository extends JpaRepository<RubriqueAllocation, Long> {
    void deleteAllByBudgetId(Long id);

    List<RubriqueAllocation> getAllByBudgetId(Long id);
    // Additional custom methods can be defined here
}
