package org.sid.commandeservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="BUDGET-SERVICE")
public interface BudgetRestClient {
}
