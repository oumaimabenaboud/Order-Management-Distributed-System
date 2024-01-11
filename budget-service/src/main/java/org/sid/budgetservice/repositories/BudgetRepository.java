package org.sid.budgetservice.repositories;
import org.sid.budgetservice.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
