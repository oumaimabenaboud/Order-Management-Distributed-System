package org.sid.productservice.web;

import lombok.AllArgsConstructor;
import org.sid.productservice.entities.Product;
import org.sid.productservice.feign.BudgetRestClient;
import org.sid.productservice.model.Rubrique;
import org.sid.productservice.repository.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController@AllArgsConstructor
@RequestMapping("/products")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BudgetRestClient budgetRestClient;

    Logger logger
            = LoggerFactory.getLogger(ProductRestController.class);

    @Autowired
    public ProductRestController( BudgetRestClient budgetRestClient) {
        this.budgetRestClient = budgetRestClient;
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return  productRepository.findAll();
    }

    @GetMapping("{id}")
    public Product getProductById(@PathVariable Long id){
        return productRepository.findById(id)
                .orElseThrow((()-> new RuntimeException(String.format("Account % not found",id))));
    }


    @GetMapping("/getListRubriqueNames")
    public List<String> getAllRubriquesNames() {
        List<Rubrique> rubriques = budgetRestClient.getAllRubriques();
        List<String> rubriqueNames = new ArrayList<>();
        //logger.error(rubriques.toString());
        for (Rubrique rubrique : rubriques) {
            rubriqueNames.add(rubrique.getNom());
        }

        return rubriqueNames;
    }

/*
    // In your Product entity
    private Long rubriqueId;
    private String rubriqueName;

    // In your ProductRestController
    private void populateProductRubriques(List<Product> products) {
        for (Product product : products) {
            Long rubriqueId = Long.valueOf(product.getRubriqueId());
            Rubrique fetchedRubrique = budgetRestClient.getRubriqueById(rubriqueId);
            if (fetchedRubrique != null) {
                product.setRubriqueNom(fetchedRubrique.getNom());
            } else {
                System.out.println("Failed to fetch rubrique with ID: " + rubriqueId);
            }
        }
    }
*/


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Product %d not found", id)));

        productRepository.delete(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product newProduct) {
        List<Rubrique> rubriquesList = budgetRestClient.getAllRubriques();
        String rubriqueName = newProduct.getRubriqueName();

        // Find the rubrique object by name
        Optional<Rubrique> optionalRubrique = rubriquesList.stream()
                .filter(rubrique -> rubrique.getNom().equals(rubriqueName))
                .findFirst();

        if (newProduct.getNom() == null || newProduct.getNom().isEmpty()) {
            return ResponseEntity.badRequest().body("Le champ 'Nom' ne peut pas être vide.");
        }
        if (newProduct.getDesc() == null || newProduct.getDesc().isEmpty()) {
            return ResponseEntity.badRequest().body("Le champ 'Description' ne peut pas être vide.");
        }
        if (optionalRubrique.isEmpty()) {
            return ResponseEntity.badRequest().body("Le champ 'Rebrique' n'existe pas");
        }

        // Set the rubrique ID in the newProduct
        Rubrique rubrique = optionalRubrique.get();
        newProduct.setRubriqueId(rubrique.getId());

        // Save and return the new product
        productRepository.save(newProduct);
        return ResponseEntity.badRequest().body("Produit ajouté avec succès !");
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        List<Rubrique> rubriqueList = budgetRestClient.getAllRubriques();

        // Check and update the fields of the existing Product with non-null values from the updatedProduct
        if (updatedProduct.getNom() != null && !updatedProduct.getNom().isEmpty()) {
            existingProduct.setNom(updatedProduct.getNom());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Nom' ne peut pas être vide.");
        }
        if (updatedProduct.getDesc() != null && !updatedProduct.getDesc().isEmpty()) {
            existingProduct.setDesc(updatedProduct.getDesc());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Description' ne peut pas être vide.");
        }

        List<String> rubriqueNames = new ArrayList<>();
        //logger.error(rubriques.toString());
        for (Rubrique rubrique : rubriqueList) {
            rubriqueNames.add(rubrique.getNom());
        }
        if (updatedProduct.getRubriqueName() != null && !updatedProduct.getRubriqueName().isEmpty() && rubriqueNames.contains(updatedProduct.getRubriqueName())) {
            existingProduct.setRubriqueName(updatedProduct.getRubriqueName());
        }else {
            return ResponseEntity.badRequest().body("Le champ 'Rubrique' ne peut pas être vide et doit appartenir à la liste des rubriques.");
        }
        // Find the rubrique object by name
        Optional<Rubrique> optionalRubrique = rubriqueList.stream()
                .filter(rubrique -> rubrique.getNom().equals(updatedProduct.getRubriqueName()))
                .findFirst();

        if (optionalRubrique.isEmpty()) {
            return ResponseEntity.badRequest().body("La rubrique avec le nom '" + updatedProduct.getRubriqueName() + "' n'existe pas.");
        }

        // Set the rubrique ID in the existingProduct
        Rubrique rubrique = optionalRubrique.get();
        existingProduct.setRubriqueId(rubrique.getId());

        // Save and return the updated Product
        productRepository.save(existingProduct);
        return ResponseEntity.ok("Produit mis à jour avec succès !");
    }
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            return productRepository.findByNomContainingIgnoreCase(searchTerm);
        } else {
            return productRepository.findAll();
        }
    }
}
