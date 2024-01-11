package org.sid.commandeservice.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.sid.commandeservice.enums.commandestype;

@Data
public class Product {

    private Long id;
    private String nom;
    private String Desc;
    private String Rubrique;
    private float PrixHT;
    private float PrixTTC;
}
