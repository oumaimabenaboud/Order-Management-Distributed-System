package org.sid.commandeservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="STRUCTURE-SERVICE")
public interface StructureRestClient {

}
