package org.sid.productservice.feign;


import org.sid.productservice.model.Rubrique;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="BUDGET-SERVICE")
public interface BudgetRestClient {


    @GetMapping("/budget-management/rubriques")
    List<Rubrique> getAllRubriques();

    @PostMapping("/budget-management/rubriques")
    Rubrique saveRubrique(@RequestBody Rubrique newRubrique);

    @GetMapping("/budget-management/rubriques/{id}")
    Rubrique getRubriqueById(@PathVariable Long id);

    @DeleteMapping("/budget-management/rubriques/{rubriqueId}")
    void deleteRubrique(@PathVariable Long rubriqueId);


    @PutMapping("/budget-management/rubriques/{id}")
    ResponseEntity<?> updateRubrique(@PathVariable Long id, @RequestBody Rubrique updatedRubrique);
}
