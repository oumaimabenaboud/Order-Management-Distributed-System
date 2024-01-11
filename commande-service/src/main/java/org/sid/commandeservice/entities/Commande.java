package org.sid.commandeservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sid.commandeservice.enums.commandestype;
import org.sid.commandeservice.model.Professeur;

import java.util.Date;
import java.util.Collection;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Commande {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date billingDate;
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private Collection<CommandeLine> commandeLines;
    @JsonIgnore
    private Long profID;
    @Transient
    private Professeur professeur;
    private double prix_total_HT;
    private double prix_total_TTC;
    @Enumerated(EnumType.STRING)
    private commandestype type;
}
