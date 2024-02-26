package org.sid.commandeservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class Budget {
    private Long id;
    private Long structureId;
    @Column(name = "budget_year")
    private int budgetYear;
    private double totalAlloue;
    private double totalRestant;
    @OneToMany
    private List<RubriqueAllocation> rubriqueAllocations;

}
