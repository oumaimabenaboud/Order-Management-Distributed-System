package org.sid.commandeservice.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import lombok.Data;
import org.sid.commandeservice.enums.structurestype;

import java.util.List;
@Data
public class Structure {
    private Long id;
    private String acronyme;
    private String nom;

    private Long idResponsable;
    private String nomResponsable;
    private double budgetAnnuel;
    private Long parentLabId;
    private String parentLabNom;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> childEquipesIds;

    @ElementCollection
    private List<String> childEquipesNoms;

    @Enumerated(EnumType.STRING)
    private structurestype type;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> equipeProfIds; // List of professor IDs

    @ElementCollection
    private List<String> equipeProfNames;

}
