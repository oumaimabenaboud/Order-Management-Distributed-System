package org.sid.structureservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Rubrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
}
