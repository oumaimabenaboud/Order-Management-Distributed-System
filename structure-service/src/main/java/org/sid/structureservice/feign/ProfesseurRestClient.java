package org.sid.structureservice.feign;

import org.sid.structureservice.model.Professeur;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="PROFESSOR-SERVICE")
public interface ProfesseurRestClient {
    @GetMapping(path="professeurs/{id}")
    Professeur getProfesseurById (@PathVariable(name="id")  Long id);
}
