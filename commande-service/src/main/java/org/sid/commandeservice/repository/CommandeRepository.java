package org.sid.commandeservice.repository;

import org.sid.commandeservice.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CommandeRepository extends JpaRepository<Commande,Long> {
}
