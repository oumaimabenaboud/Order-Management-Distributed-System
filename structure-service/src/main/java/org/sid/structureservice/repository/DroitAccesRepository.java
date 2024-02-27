package org.sid.structureservice.repository;

import org.sid.structureservice.entities.DroitAcces;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DroitAccesRepository extends JpaRepository<DroitAcces,Long> {

    List<DroitAcces> findByIdProfessor(Long professorId);

    List<DroitAcces> findByIdStructure(Long structureId);

    DroitAcces findByIdProfessorAndIdStructure(Long professorId, Long structureId);

    void deleteByIdStructure(Long Idstructure);
}
