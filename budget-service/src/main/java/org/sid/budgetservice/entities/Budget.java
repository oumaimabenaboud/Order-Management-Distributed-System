package org.sid.budgetservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long structureId;
    private Year year;
    private double totalAlloue;
    private double totalRestant;
    @OneToMany
    private List<RubriqueAllocation> rubriqueAllocations;

}
