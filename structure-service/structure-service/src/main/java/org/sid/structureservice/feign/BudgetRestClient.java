package org.sid.structureservice.feign;

import org.sid.structureservice.model.Budget;
import org.sid.structureservice.model.Rubrique;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="BUDGET-SERVICE")
public interface BudgetRestClient {
    @GetMapping("/budget-management")
    List<Budget> getAllBudgets();

    @PostMapping("/budget-management")
    Budget createBudget();

    @GetMapping("/budget-management/{id}")
    Budget getBudgetById(@PathVariable Long id);

    @DeleteMapping("/budget-management/{id}")
    void deleteBudget(@PathVariable Long id);

    @GetMapping("/budget-management/rubriques")
    List<Rubrique> getAllRubriques();

    @PostMapping("/budget-management/rubriques")
    Rubrique saveRubrique(@RequestBody Rubrique newRubrique);

    @GetMapping("/budget-management/budgets/{budgetId}/rubriques")
    List<Rubrique> getRubriquesInBudget(@PathVariable Long budgetId);

    @PostMapping("/budget-management/budgets/{budgetId}/rubriques")
    Rubrique addRubriqueToBudget(@PathVariable Long budgetId, @RequestBody Rubrique rubrique);

    @DeleteMapping("/budget-management/rubriques/{rubriqueId}")
    void deleteRubrique(@PathVariable Long rubriqueId);

    @PutMapping("/budget-management/{budgetId}")
    ResponseEntity<Budget> updateBudget(@PathVariable Long budgetId, @RequestBody Budget updatedBudget);

    @GetMapping("/budget-management/{budgetId}/rubriques")
    ResponseEntity<List<Rubrique>> getAllRubriquesForBudget(@PathVariable Long budgetId);

    @GetMapping("/budget-management/rubriques/{id}")
    Rubrique getRubriqueById(@PathVariable Long id);
}
