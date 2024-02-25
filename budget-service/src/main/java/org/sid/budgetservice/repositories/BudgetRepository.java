package org.sid.budgetservice.repositories;
import org.sid.budgetservice.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Budget findByStructureId(Long id);
}
