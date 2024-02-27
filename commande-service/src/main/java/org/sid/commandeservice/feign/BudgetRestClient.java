package org.sid.commandeservice.feign;

import org.sid.commandeservice.model.Budget;
import org.sid.commandeservice.model.RubriqueAllocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name="BUDGET-SERVICE")
public interface BudgetRestClient {

    @GetMapping(path="budget-management/budget/byStructure/{id}")
    List<Budget> getBudgetByStructureId(@PathVariable Long id);
    @GetMapping(path="budget-management/budget/{id}")
    Budget getBudgetById(@PathVariable Long id);

    @PutMapping("budget-management/budget/updateAllocations/{budgetId}")
    ResponseEntity<?> updateAllocations(@PathVariable Long budgetId, @RequestBody List<RubriqueAllocation> updatedAllocations);
}
