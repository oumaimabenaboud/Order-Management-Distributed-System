package org.sid.commandeservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class CommandeLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double total_prixHT_ligne;
    private double total_prixTTC_ligne;
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    private Commande commande;
    @JsonIgnore
    @Transient
    private org.sid.commandeservice.model.Product product;
    @Transient
    private String productName;

}
