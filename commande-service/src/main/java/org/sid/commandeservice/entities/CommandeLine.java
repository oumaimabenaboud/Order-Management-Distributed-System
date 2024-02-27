package org.sid.commandeservice.entities;

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
    private double prixHT;
    private double prixTTC;
    private String productName;
    private Long productId;
    private Long produitRubriqueId;
    private Long commandeId;
}
