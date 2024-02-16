package org.sid.structureservice.repository;

import org.sid.structureservice.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface StructureRepository extends JpaRepository<Structure,Long> {
}
