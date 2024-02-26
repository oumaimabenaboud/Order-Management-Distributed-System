package org.sid.commandeservice.model;

import lombok.Data;

@Data
public class RubriqueAllocation {
    private Long id;
    private Long rubriqueId;
    private Long budgetId;
    private String rubriqueName;
    private double montantAlloue;
    private double montantRestant;

}
