package org.sid.budgetservice.repositories;
import org.sid.budgetservice.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Override
    List<Budget> findAll();

    List<Budget> findByStructureId(Long id);

    Budget getBudgetById(Long id);

    Budget getBudgetByStructureIdAndBudgetYear(Long id, int year);
}
