package org.sid.structureservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class Rubrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private double allocatedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Budget budget;
}
