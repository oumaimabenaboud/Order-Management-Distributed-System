package org.sid.professeur.repositories;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.sid.professeur.entities.professeur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProfesseurRepo extends JpaRepository<professeur, Long> {
    Optional<professeur> findByMail(String mail);

    List<professeur> findByPrenomContainingIgnoreCaseOrNomContainingIgnoreCase(String searchTerm, String searchTerm1);

    List<professeur> findByPrenomContainingIgnoreCaseAndIsAdminFalseOrNomContainingIgnoreCaseAndIsAdminFalse(String prenom, String nom);

    List<professeur> findAllByIsAdminFalse();
}
