package org.sid.commandeservice.web;

import lombok.AllArgsConstructor;
import org.sid.commandeservice.entities.Commande;
import org.sid.commandeservice.entities.CommandeLine;
import org.sid.commandeservice.feign.ProductRestClient;
import org.sid.commandeservice.feign.ProfesseurRestClient;
import org.sid.commandeservice.model.Product;
import org.sid.commandeservice.model.Professeur;
import org.sid.commandeservice.repository.CommandeRepository;
import org.sid.commandeservice.repository.CommandeLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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


    @GetMapping(path = "{id}")
    public Commande getCommande(@PathVariable(name="id") Long id){
        Commande commande= commandeRepository.findById(id).get();
        System.out.println(commande.getProfId());
        //System.out.println(commande.getCommandeLines());
        Professeur professeur = professeurRestClient.getProfesseurById(commande.getProfId());
        commande.getCommandeLines().forEach(pi->{
            Product product = productRestClient.getProductById(pi.getId());
            //pi.setProduct(product);
            pi.setProductName(product.getNom());
        });
        return commande;

    }

    @PostMapping
    public Commande addCommande(@RequestBody Commande nouvelleCommande) {
        Professeur professeur = nouvelleCommande.getProfId();

        if (professeur != null && professeur.getId() != null) {
            if (!professeur.isDroit_daccee()) {
                throw new RuntimeException("Access denied!");
            }

            double totalHT = 0.0;
            double totalTTC = 0.0;

            // Save the Commande first
            Commande savedCommande = commandeRepository.save(nouvelleCommande);

            for (CommandeLine commandeLine : nouvelleCommande.getCommandeLines()) {
                String productName = commandeLine.getProductName();
                System.out.println("Product name: " + productName); // Debug print

                Product product = productRestClient.getProductByName(productName);

                if (product == null) {
                    throw new RuntimeException("Product '" + productName + "' not found!");
                }

                CommandeLine newCommandeLine = new CommandeLine();
                newCommandeLine.setId(product.getId());
                newCommandeLine.setQuantity(commandeLine.getQuantity());
                newCommandeLine.setTotal_prixHT_ligne(product.getPrixHT() * commandeLine.getQuantity());
                newCommandeLine.setTotal_prixTTC_ligne(product.getPrixTTC() * commandeLine.getQuantity());
                newCommandeLine.setProduct(product);
                newCommandeLine.setCommande(savedCommande); // Associate with the saved Commande

                CommandeLine savedCommandeLine = commandeLineRepository.save(newCommandeLine);

                totalHT += savedCommandeLine.getTotal_prixHT_ligne();
                totalTTC += savedCommandeLine.getTotal_prixTTC_ligne();

                System.out.println("Product ID: " + product.getId()); // Debug print
                System.out.println("CommandeLine Quantity: " + commandeLine.getQuantity()); // Debug print
                // Add more debug prints as needed
            }

            savedCommande.setPrix_total_HT(totalHT);
            savedCommande.setPrix_total_TTC(totalTTC);
            savedCommande.setProfID(professeur.getId());

            return commandeRepository.save(savedCommande);
        } else {
            throw new RuntimeException("Invalid Professeur or ID!");
        }
    }

    @PutMapping("/{id}")
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

        for (Commande commande : commandes) {
            System.out.println(commande.getProfID());
            Professeur professeur = professeurRestClient.getProfesseurById(commande.getProfID());
            commande.setProfesseur(professeur);

            commande.getCommandeLines().forEach(pi -> {
                Product product = productRestClient.getProductById(pi.getId());
                pi.setProductName(product.getNom());
            });
        }

        return commandes;
    }

}
