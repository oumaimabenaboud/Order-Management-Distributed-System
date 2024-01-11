package org.sid.structureservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class ResponsableStructure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "respo", cascade = CascadeType.ALL)
    private Collection<Structure> structures;
    @JsonIgnore
    @Transient
    private org.sid.structureservice.model.Professeur professeur;
}
