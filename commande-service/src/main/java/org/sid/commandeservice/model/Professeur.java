package org.sid.commandeservice.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Professeur {
    private Long id;
    private String nom;
    private String prenom;
    private String mail;
    private String mdp;
    private boolean droit_daccee;
    private boolean first_cnx;
    private boolean isAdmin;
}
