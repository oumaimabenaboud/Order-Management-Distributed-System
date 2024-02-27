package org.sid.commandeservice.feign;

import org.sid.commandeservice.model.Budget;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="BUDGET-SERVICE")
public interface BudgetRestClient {

    @GetMapping(path="/budget/byStructure/{id}")
    List<Budget> getBudgetByStructureId(@PathVariable Long id);
    @GetMapping(path="/budget/{id}")
    Budget getBudgetById(@PathVariable Long id);
}
