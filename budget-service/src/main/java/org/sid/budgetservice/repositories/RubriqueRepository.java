package org.sid.budgetservice.repositories;

import org.sid.budgetservice.entities.Rubrique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RubriqueRepository extends JpaRepository<Rubrique, Long> {
    List<Rubrique> findByNomContainingIgnoreCase(String searchTerm);
}
