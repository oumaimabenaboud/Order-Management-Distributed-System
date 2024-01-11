package org.sid.structureservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.model.Professeur;
import java.util.Collection;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Structure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String acronyme;
    private String nom;
    @ManyToOne
    private ResponsableStructure responsableStructure;
    @JsonIgnore
    private Long respoID;
    private double budget;
    @Enumerated(EnumType.STRING)
    private structurestype type;
    @Transient
    private Collection<Professeur> professeurs;
}
