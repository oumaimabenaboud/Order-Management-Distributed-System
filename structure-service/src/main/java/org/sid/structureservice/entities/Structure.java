package org.sid.structureservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.model.Professeur;
import java.util.Collection;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Structure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String acronyme;
    private String nom;

    private Long responsibleId;
    private double budget;
    private Long parentLabId;

    @Enumerated(EnumType.STRING)
    private structurestype type;
    @Transient
    private Collection<Professeur> equipe_prof;
}
