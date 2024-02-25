package org.sid.productservice.repository;

import org.sid.productservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByNom(String name);

    List<Product> findByNomContainingIgnoreCase(String searchTerm);
}
