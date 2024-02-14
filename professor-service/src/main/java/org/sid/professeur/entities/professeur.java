package org.sid.professeur.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class professeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Change the type to Long

    private String nom;
    private String prenom;
    private String mail;
    private String mdp;
    private boolean droit_daccee;
    private boolean first_cnx;

}

