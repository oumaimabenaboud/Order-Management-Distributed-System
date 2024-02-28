package org.sid.commandeservice.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sid.commandeservice.entities.Commande;
import org.sid.commandeservice.entities.CommandeLine;
import org.sid.commandeservice.enums.commandestype;
import org.sid.commandeservice.feign.BudgetRestClient;
import org.sid.commandeservice.feign.ProductRestClient;
import org.sid.commandeservice.feign.ProfesseurRestClient;
import org.sid.commandeservice.model.Budget;
import org.sid.commandeservice.model.Product;
import org.sid.commandeservice.model.Professeur;
import org.sid.commandeservice.model.RubriqueAllocation;
import org.sid.commandeservice.repository.CommandeRepository;
import org.sid.commandeservice.repository.CommandeLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController@AllArgsConstructor
@RequestMapping("/commandes")
public class CommandeRestController {
    @Autowired
    private CommandeRepository commandeRepository;
    private CommandeLineRepository commandeLineRepository;
    @Autowired
    private ProfesseurRestClient professeurRestClient;
    @Autowired
    private ProductRestClient productRestClient;
    @Autowired
    private BudgetRestClient budgetRestClient;

    @GetMapping( "{id}")
    public Commande getCommande(@PathVariable Long id){
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with structure id: " + id));
    }
    @PostMapping
    public ResponseEntity<?> addCommande(@RequestBody Commande nouvelleCommande) {
        Commande newCommand = new Commande();
        if(nouvelleCommande.getStructureId() !=null){
        newCommand.setStructureId(nouvelleCommande.getStructureId());
        }else{
            return ResponseEntity.badRequest().body("Aucune structure détectée, veuillez réessayer plus tard.");
        }
        if(nouvelleCommande.getBudgetId() !=null){
            newCommand.setBudgetId(nouvelleCommande.getBudgetId());
        }else{
            return  ResponseEntity.badRequest().body("Une erreur s'est produite. Veuillez réessayer plus tard.");
        }
        // Check if prixTotalHT is less than prixTotalTTC
        if (nouvelleCommande.getPrixTotalTTC() < nouvelleCommande.getPrixTotalHT()) {
            return ResponseEntity.badRequest().body("Le montant hors taxe doit être inférieur au montant toutes taxes comprises (TTC).");
        }
        newCommand.setCommandeDate((Date) nouvelleCommande.getCommandeDate());
        newCommand.setProfId(nouvelleCommande.getProfId());
        commandeRepository.save(newCommand);
        Long budgetId = nouvelleCommande.getBudgetId();
        Budget budget = budgetRestClient.getBudgetById(budgetId);
        List<RubriqueAllocation> rubriquesAllocations = budget.getRubriqueAllocations();
        List<CommandeLine> commandeLines = nouvelleCommande.getCommandeLines();
        for(CommandeLine commandeLine : commandeLines){
            for(RubriqueAllocation rubriqueAllocation : rubriquesAllocations){
                if (rubriqueAllocation.getRubriqueId()== commandeLine.getProduitRubriqueId()){
                    if (commandeLine.getQuantity()<= 0 || commandeLine.getPrixHT()<= 0 || commandeLine.getPrixTTC()<= 0){
                        return ResponseEntity.badRequest().body("Les valeurs de la quantité, du prix hors taxe (HT) ou du prix toutes taxes comprises (TTC) doivent être supérieures à zéro.");

                    }
                    if (commandeLine.getProductId() == null ){
                        return ResponseEntity.badRequest().body("Veuillez choisir un produit parmi la liste des produits disponibles !");
                    }
                    if(rubriqueAllocation.getMontantRestant()<commandeLine.getPrixTTC()*commandeLine.getQuantity()){
                        return ResponseEntity.badRequest().body("Vous avez dépassé le montant alloué à la rubrique :"+ rubriqueAllocation.getRubriqueName());
                    }else{
                        rubriqueAllocation.setMontantRestant(Math.round((rubriqueAllocation.getMontantRestant() - commandeLine.getPrixTTC() * commandeLine.getQuantity()) * 100.0) / 100.0);
                        commandeLine.setCommandeId(newCommand.getId());
                        commandeLineRepository.save(commandeLine);
                    }
                }
            }
        }
        if(commandeLines.isEmpty()){
            commandeRepository.deleteById(newCommand.getId());
            return ResponseEntity.badRequest().body("Aucun produit n'a été détecté dans cette commande. La commande est vide.");
        }else {
            newCommand.setCommandeLines(commandeLines);
            if (nouvelleCommande.getPrixTotalHT() > budget.getTotalRestant()) {
                return ResponseEntity.badRequest().body("Le montant de la commande dépasse votre budget annuel restant");
            } else {
                newCommand.setPrixTotalHT(nouvelleCommande.getPrixTotalHT());
            }
            if (nouvelleCommande.getPrixTotalTTC() > budget.getTotalRestant()) {
                return ResponseEntity.badRequest().body("Le montant de la commande dépasse votre budget annuel restant");
            } else {
                newCommand.setPrixTotalTTC(nouvelleCommande.getPrixTotalTTC());
            }

            newCommand.setType(commandestype.EN_COURS);
            budgetRestClient.updateAllocations(budgetId, rubriquesAllocations);
            commandeRepository.save(newCommand);
            return ResponseEntity.ok("Commande ajoutée avec succès");
        }
    }

    /*@PutMapping("/{id}")
    public Commande updateCommande(@PathVariable Long id, @RequestBody Commande updatedCommande) {
        Commande existingCommande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));

        Professeur professeur = updatedCommande.getProfesseur();

        if (professeur != null && professeur.getId() != null && professeur.isDroit_daccee()) {
            existingCommande.setProfID(professeur.getId());

            List<CommandeLine> updatedCommandeLines = new ArrayList<>();

            for (CommandeLine updatedCommandeLine : updatedCommande.getCommandeLines()) {
                String productName = updatedCommandeLine.getProductName();
                Product product = productRestClient.getProductByName(productName);

                if (product == null) {
                    throw new RuntimeException("Product '" + productName + "' not found!");
                }

                CommandeLine existingCommandeLine = existingCommande.getCommandeLines().stream()
                        .filter(cl -> cl.getId().equals(updatedCommandeLine.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingCommandeLine != null) {
                    // Update existing CommandeLine
                    existingCommandeLine.setQuantity(updatedCommandeLine.getQuantity());
                    existingCommandeLine.setTotal_prixHT_ligne(product.getPrixHT() * existingCommandeLine.getQuantity());
                    existingCommandeLine.setTotal_prixTTC_ligne(product.getPrixTTC() * existingCommandeLine.getQuantity());

                    // Save and add to the updatedCommandeLines list
                    updatedCommandeLines.add(commandeLineRepository.save(existingCommandeLine));
                } else {
                    // Create a new CommandeLine if not found
                    CommandeLine newCommandeLine = new CommandeLine();
                    newCommandeLine.setId(product.getId());
                    newCommandeLine.setQuantity(updatedCommandeLine.getQuantity());
                    newCommandeLine.setTotal_prixHT_ligne(product.getPrixHT() * newCommandeLine.getQuantity());
                    newCommandeLine.setTotal_prixTTC_ligne(product.getPrixTTC() * newCommandeLine.getQuantity());
                    newCommandeLine.setProduct(product);
                    newCommandeLine.setCommande(existingCommande);

                    // Save and add to the updatedCommandeLines list
                    updatedCommandeLines.add(commandeLineRepository.save(newCommandeLine));
                }
            }

            // Remove command lines that exist in the current Commande but are not present in the updated version
            List<CommandeLine> linesToRemove = existingCommande.getCommandeLines().stream()
                    .filter(cl -> updatedCommandeLines.stream().noneMatch(updatedCL -> updatedCL.getId().equals(cl.getId())))
                    .collect(Collectors.toList());

            commandeLineRepository.deleteAll(linesToRemove);

            // Calculate the totals after processing all updated command lines
            double totalHT = updatedCommandeLines.stream().mapToDouble(CommandeLine::getTotal_prixHT_ligne).sum();
            double totalTTC = updatedCommandeLines.stream().mapToDouble(CommandeLine::getTotal_prixTTC_ligne).sum();

            // Set the updatedCommandeLines and update the total prices
            existingCommande.setCommandeLines(updatedCommandeLines);
            existingCommande.setPrix_total_HT(totalHT);
            existingCommande.setPrix_total_TTC(totalTTC);

            // Save and return the updated Commande
            return commandeRepository.save(existingCommande);
        } else {
            throw new RuntimeException("Invalid Professeur or ID!");
        }
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommande(@PathVariable Long id) {
        Commande existingCommande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));

        // Delete the command and return a response
        commandeRepository.delete(existingCommande);
        return ResponseEntity.ok("Commande with ID: " + id + " has been deleted successfully");
    }*/


    @DeleteMapping("{id}")
    public void deleteCommande(@PathVariable String id){
        commandeRepository.deleteById(Long.valueOf(id));
    }

    @GetMapping
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandes;
    }

    @GetMapping("/byStructure/{structureId}")
    public List<Commande> getCommandesByStructureId(@PathVariable Long structureId) {
        return commandeRepository.getCommandesByStructureId(structureId);
    }

}
