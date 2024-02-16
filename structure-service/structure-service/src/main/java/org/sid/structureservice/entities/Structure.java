package org.sid.structureservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.model.Professeur;
import java.util.Collection;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Structure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String acronyme;
    private String nom;

    private Long idResponsable;
    private String nomResponsable;
    private double budget;
    private Long parentLabId;

    @Enumerated(EnumType.STRING)
    private structurestype type;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> equipe_prof_ids; // List of professor IDs

    @ElementCollection
    private List<String> equipe_prof_names;
}
