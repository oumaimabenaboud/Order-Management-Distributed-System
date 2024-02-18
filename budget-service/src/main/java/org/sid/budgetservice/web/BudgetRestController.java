package org.sid.budgetservice.web;
import org.sid.budgetservice.repositories.BudgetRepository;
import org.sid.budgetservice.entities.*;
import org.sid.budgetservice.repositories.RubriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/budget-management")
public class BudgetRestController {

    @Autowired
    private RubriqueRepository rubriqueRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @PostMapping
    public Budget createBudget() {
        Budget budget = new Budget();
        budget.setRubriques(new ArrayList<>()); // Default with 0 rubriques
        return budgetRepository.save(budget);
    }


    @GetMapping("/{id}")
    public Budget getBudgetById(@PathVariable Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
    }
    // Delete a budget
    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetRepository.deleteById(id);
    }

    @GetMapping("/rubriques")
    public List<Rubrique> getAllRubriques() {
        return rubriqueRepository.findAll();
    }
    @PostMapping("/rubriques")
    public Rubrique saveRubrique(@RequestBody Rubrique newRubrique) {
        return rubriqueRepository.save(newRubrique);
    }
    @PutMapping("/rubriques/{id}")
    public Rubrique updateRubrique(@PathVariable Long id, @RequestBody Rubrique updatedRubrique) {
        Rubrique existingRubrique = rubriqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Rubrique not found"));

        // Update the properties if provided
        if (updatedRubrique.getNom() != null) {
            existingRubrique.setNom(updatedRubrique.getNom());
    }
        return rubriqueRepository.save(existingRubrique);
    }
    // List all rubriques in a budget
    /*@GetMapping("/budgets/{budgetId}/rubriques")
    public List<Rubrique> getRubriquesInBudget(@PathVariable Long budgetId) {
        return rubriqueRepository.findByBudgetId(budgetId);
    }*/
    @PostMapping("/budgets/{budgetId}/rubriques")
    public Rubrique addRubriqueToBudget(@PathVariable Long budgetId, @RequestBody Rubrique rubrique) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId));
        rubrique.setBudget_id(budget.getId());
        return rubriqueRepository.save(rubrique);
    }

    @GetMapping("/rubriques/search")
    public List<Rubrique> searchRubriques(@RequestParam(required = false) String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            return rubriqueRepository.findByNomContainingIgnoreCase(searchTerm);
        } else {
            return rubriqueRepository.findAll();
        }
    }

    // Delete a rubrique
    @DeleteMapping("/rubriques/{rubriqueId}")
    public void deleteRubrique(@PathVariable Long rubriqueId) {
        rubriqueRepository.deleteById(rubriqueId);
    }
    // Update an existing budget
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long budgetId, @RequestBody Budget updatedBudget) {
        Optional<Budget> existingBudgetOptional = budgetRepository.findById(budgetId);

        if (existingBudgetOptional.isPresent()) {
            Budget existingBudget = existingBudgetOptional.get();
            existingBudget.setTotalAmount(updatedBudget.getTotalAmount());
            // You might need to handle rubriques here

            Budget savedBudget = budgetRepository.save(existingBudget);
            return ResponseEntity.ok(savedBudget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/{budgetId}/rubriques")
    public ResponseEntity<List<Rubrique>> getAllRubriquesForBudget(@PathVariable Long budgetId) {
        Optional<Budget> budgetOptional = budgetRepository.findById(budgetId);

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();
            List<Rubrique> rubriques = new ArrayList<>(budget.getRubriques());
            return ResponseEntity.ok(rubriques);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/rubriques/{id}")
    public Rubrique getRubriqueById(@PathVariable Long id) {
        return rubriqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubrique not found with id: " + id));
    }
}
