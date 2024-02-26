package org.sid.structureservice.repository;

import org.sid.structureservice.entities.DroitAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DroitAccessRepository extends JpaRepository<DroitAccess,Long> {

    List<DroitAccess> findByIdProfessor(Long professorId);

    List<DroitAccess> findByIdStructure(Long structureId);

    List<DroitAccess> findByIdProfessorAndIdStructure(Long professorId, Long structureId);
}
