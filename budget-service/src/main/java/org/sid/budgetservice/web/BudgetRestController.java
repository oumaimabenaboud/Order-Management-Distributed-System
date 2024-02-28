package org.sid.budgetservice.web;

import org.sid.budgetservice.repositories.BudgetRepository;
import org.sid.budgetservice.entities.*;
import org.sid.budgetservice.repositories.RubriqueAllocationRepository;
import org.sid.budgetservice.repositories.RubriqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/budget-management")
public class BudgetRestController {

    @Autowired
    private RubriqueRepository rubriqueRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private RubriqueAllocationRepository rubriqueAllocationRepository;


    //Rubriques

    @GetMapping("/rubriques")
    public List<Rubrique> getAllRubriques() {
        return rubriqueRepository.findAll();
    }
    @GetMapping("/rubriques/{id}")
    public Rubrique getRubriqueById(@PathVariable Long id) {
        return rubriqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rubrique not found with id: " + id));
    }
    @PostMapping("/rubriques")
    public Rubrique saveRubrique(@RequestBody Rubrique newRubrique) {
        return rubriqueRepository.save(newRubrique);
    }

    @PutMapping("/rubriques/{id}")
    public ResponseEntity<?> updateRubrique(@PathVariable Long id, @RequestBody Rubrique updatedRubrique) {
        Rubrique existingRubrique = rubriqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Rubrique not found"));

        // Update the properties if provided
        if (updatedRubrique.getNom() != null && !updatedRubrique.getNom().isEmpty()) {
            existingRubrique.setNom(updatedRubrique.getNom());
        }else {
            return ResponseEntity.badRequest().body("Le champ Nom de rubrique ne peut pas être vide.");
        }
        rubriqueRepository.save(existingRubrique);

        return ResponseEntity.ok("Structure mise à jour avec succès !");
    }
    @DeleteMapping("/rubriques/{rubriqueId}")
    public void deleteRubrique(@PathVariable Long rubriqueId) {
        rubriqueRepository.deleteById(rubriqueId);
    }
    @GetMapping("/rubriques/search")
    public List<Rubrique> searchRubriques(@RequestParam(required = false) String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            return rubriqueRepository.findByNomContainingIgnoreCase(searchTerm);
        } else {
            return rubriqueRepository.findAll();
        }
    }

    //Budget
    @GetMapping("/budget")
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @PostMapping("/budget")
    public Budget createBudget(@RequestBody Budget newBudget) {
        Budget budget = new Budget();
        budget.setBudgetYear(newBudget.getBudgetYear());
        budget.setStructureId(newBudget.getStructureId());
        budget.setTotalAlloue(newBudget.getTotalAlloue());
        budget.setTotalRestant(newBudget.getTotalRestant());
        budgetRepository.save(budget);

        // Iterate over the rubriqueAllocations and set the budgetId
        for (RubriqueAllocation rubriqueAllocation : newBudget.getRubriqueAllocations()) {
            rubriqueAllocation.setBudgetId(budget.getId());
            rubriqueAllocation.setMontantRestant(rubriqueAllocation.getMontantAlloue());
        }

        // Save all rubriqueAllocations
        rubriqueAllocationRepository.saveAll(newBudget.getRubriqueAllocations());

        // Set the rubriqueAllocations to the budget
        budget.setRubriqueAllocations(newBudget.getRubriqueAllocations());

        // Save the updated budget
        return budgetRepository.save(budget);
    }

    @PutMapping("/budget/{id}")
    @Transactional
    public Budget updateBudget(@PathVariable Long id, @RequestBody Budget newBudget) {
        Budget existingBudget = budgetRepository.getBudgetById(id);
        System.out.println(existingBudget);

        existingBudget.setBudgetYear(newBudget.getBudgetYear());
        existingBudget.setStructureId(newBudget.getStructureId());
        existingBudget.setTotalAlloue(newBudget.getTotalAlloue());

        List<RubriqueAllocation> existingAllocations = rubriqueAllocationRepository.getAllByBudgetId(existingBudget.getId());
        List<RubriqueAllocation> updatedAllocations = newBudget.getRubriqueAllocations();
        List<RubriqueAllocation> rubriqueAllocations = new ArrayList<>();

        // Delete rubriqueAllocations that are not present in the updatedAllocations
        for (RubriqueAllocation existingAllocation : existingAllocations) {
            boolean found = false;
            for (RubriqueAllocation updatedAllocation : updatedAllocations) {
                if (existingAllocation.getId().equals(updatedAllocation.getId())) {
                    existingAllocation.setMontantAlloue(updatedAllocation.getMontantAlloue());
                    rubriqueAllocations.add(existingAllocation);
                    found = true;
                    break;
                }
            }
            if (!found) {
                rubriqueAllocationRepository.deleteById(existingAllocation.getId());
            }
        }

        // Add new rubriqueAllocations
        for (RubriqueAllocation updatedAllocation : updatedAllocations) {
            if (updatedAllocation.getId() == null) {
                updatedAllocation.setBudgetId(existingBudget.getId());
                rubriqueAllocations.add(updatedAllocation);
            }
        }

        // Save all updated rubriqueAllocations
        rubriqueAllocationRepository.saveAll(rubriqueAllocations);

        // Set the updated rubriqueAllocations to the existing budget
        existingBudget.setRubriqueAllocations(rubriqueAllocations);

        // Save and return the updated budget
        return budgetRepository.save(existingBudget);
    }


    @GetMapping("/budget/{id}")
    public Budget getBudgetById(@PathVariable Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with structure id: " + id));
    }
    @GetMapping("/budget/byStructure/{id}")
    public List<Budget> getBudgetByStructureId(@PathVariable Long id) {
        List<Budget> budgets = budgetRepository.findByStructureId(id);
        if (budgets != null || !budgets.isEmpty() ) {
            return budgets;
        }else{
            return null;
        }
    }

    // Delete a budget
    @DeleteMapping("budget/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetRepository.deleteById(id);
    }
    @PutMapping("/budget/updateAllocations/{budgetId}")
    public Budget updateAllocations(@PathVariable Long budgetId, @RequestBody List<RubriqueAllocation> updatedAllocations) {
        // Retrieve the budget
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId));

        // Get the existing rubriqueAllocations
        List<RubriqueAllocation> existingAllocations = budget.getRubriqueAllocations();

        // Update the existing allocations with the new values
        for (RubriqueAllocation updatedAllocation : updatedAllocations) {
            for (RubriqueAllocation existingAllocation : existingAllocations) {
                if (existingAllocation.getId().equals(updatedAllocation.getId())) {
                    // Update the montantAlloue
                    existingAllocation.setMontantRestant(updatedAllocation.getMontantRestant());
                    rubriqueAllocationRepository.save(existingAllocation);
                }
            }
        }

        // Recalculate the total allocated amount of the budget
        double totalRestant = existingAllocations.stream()
                .mapToDouble(RubriqueAllocation::getMontantRestant)
                .sum();
        totalRestant = Math.round(totalRestant * 100.0) / 100.0;
        budget.setTotalRestant(totalRestant);

        // Save the updated budget
        return budgetRepository.save(budget);
    }

}
