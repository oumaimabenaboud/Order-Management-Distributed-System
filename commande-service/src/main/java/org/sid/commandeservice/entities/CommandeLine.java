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
    private double total_prixHT_ligne;
    private double total_prixTTC_ligne;
    private String productName;
    private Long productId;
    private Long commandeId;
}
