package org.sid.commandeservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sid.commandeservice.enums.commandestype;
import org.sid.commandeservice.model.Professeur;

import java.util.Date;
import java.util.Collection;
import java.util.List;

@Entity @Builder
@Data @NoArgsConstructor @AllArgsConstructor
public class Commande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date commandeDate;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CommandeLine> commandeLines;
    private Long profId;
    private Long structureId;
    private Long budgetId;
    private double prixTotalHT;
    private double prixTotalTTC;
    @Enumerated(EnumType.STRING)
    private commandestype type;

}
