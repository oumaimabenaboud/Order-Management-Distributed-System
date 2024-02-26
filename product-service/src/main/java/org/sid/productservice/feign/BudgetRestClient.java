package org.sid.productservice.feign;



import org.sid.productservice.model.Rubrique;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="BUDGET-SERVICE")
public interface BudgetRestClient {


        @DeleteMapping("/budget-management/{id}")
        void deleteBudget(@PathVariable("id") Long id);

        @GetMapping("/budget-management/rubriques")
        List<Rubrique> getAllRubriques();

        @GetMapping("/budget-management/rubriques/{id}")
        Rubrique getRubriqueById(@PathVariable("id") Long id);

        @PostMapping("/budget-management/rubriques")
        Rubrique saveRubrique(@RequestBody Rubrique newRubrique);

        @PutMapping("/budget-management/rubriques/{id}")
        ResponseEntity<?> updateRubrique(@PathVariable("id") Long id, @RequestBody Rubrique updatedRubrique);

        @PostMapping("/budget-management/budgets/{budgetId}/rubriques")
        Rubrique addRubriqueToBudget(@PathVariable("budgetId") Long budgetId, @RequestBody Rubrique rubrique);

        @GetMapping("/budget-management/rubriques/search")
        List<Rubrique> searchRubriques(@RequestParam(required = false, name = "searchTerm") String searchTerm);

        @DeleteMapping("/budget-management/rubriques/{rubriqueId}")
        void deleteRubrique(@PathVariable("rubriqueId") Long rubriqueId);

        @GetMapping("/budget-management/{budgetId}/rubriques")
        ResponseEntity<List<Rubrique>> getAllRubriquesForBudget(@PathVariable("budgetId") Long budgetId);
    }
