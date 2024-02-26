package org.sid.commandeservice.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.sid.commandeservice.enums.commandestype;

@Data
public class Product {
    private long id;
    private String nom;
    private String desc;
    private String rubriqueName;
    private Long rubriqueId;
}
