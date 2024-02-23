package org.sid.structureservice.repository;

import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.enums.structurestype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface StructureRepository extends JpaRepository<Structure,Long> {
    List<Structure> findByType(structurestype type);
    Structure getStructureById (Long id);
    List<Structure> findByIdResponsable(Long professorId);
    List<Structure> findByEquipeProfIdsContains(Long professorId);
    List<Structure> findByNomContainingIgnoreCase(String searchTerm);

}
