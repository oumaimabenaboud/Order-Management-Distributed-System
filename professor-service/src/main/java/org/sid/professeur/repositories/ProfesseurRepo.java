package org.sid.professeur.repositories;

import org.sid.professeur.entities.professeur;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfesseurRepo extends JpaRepository<professeur, Long> {
}
