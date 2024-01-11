package org.sid.commandeservice.repository;

import org.sid.commandeservice.entities.CommandeLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CommandeLineRepository extends JpaRepository<CommandeLine,Long> {
    //public Collection<ProductItem> findByBillId(Long id);
}
