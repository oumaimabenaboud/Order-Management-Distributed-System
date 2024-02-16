package org.sid.structureservice.repository;

import org.sid.structureservice.entities.ResponsableStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ResponsableStructureRepository extends JpaRepository<ResponsableStructure,Long> {
    //public Collection<ProductItem> findByBillId(Long id);
}
