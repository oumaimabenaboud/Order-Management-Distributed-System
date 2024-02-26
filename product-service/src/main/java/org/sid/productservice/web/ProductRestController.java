package org.sid.productservice.web;

import lombok.AllArgsConstructor;
import org.sid.productservice.entities.Product;
import org.sid.productservice.feign.BudgetRestClient;
import org.sid.productservice.model.Rubrique;
import org.sid.productservice.repository.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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


    public List<Rubrique> getAllRubriques() {
        //List<String> rubriqueNames = getAllRubriqueNames();

        return budgetRestClient.getAllRubriques();
    }
    @GetMapping("/getListRubriqueNames")
    public List<String> getAllRubriqueNames() {
        List<Rubrique> rubriques = budgetRestClient.getAllRubriques();
        List<String> rubriqueNames = new ArrayList<>();
        logger.error(rubriques.toString());
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
    public Product createProduct(@RequestBody Product newProduct) {
        List<String> rubriqueNames = getAllRubriqueNames();

        logger.error(rubriqueNames.toString());

        if (!rubriqueNames.contains(newProduct.getRubrique())) {
            throw new RuntimeException("Rubrique name does not exist: " + newProduct.getRubrique());
        }
        return productRepository.save(newProduct);
    }


    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        List<String> rubriqueNames = getAllRubriqueNames();

        // Check and update the fields of the existing Product with non-null values from the updatedProduct
        if (updatedProduct.getNom() != null && !updatedProduct.getNom().isEmpty()) {
            existingProduct.setNom(updatedProduct.getNom());
        }
        if (updatedProduct.getDesc() != null && !updatedProduct.getDesc().isEmpty()) {
            existingProduct.setDesc(updatedProduct.getDesc());
        }
        if (updatedProduct.getRubrique() != null && !updatedProduct.getRubrique().isEmpty() && !rubriqueNames.contains(updatedProduct.getRubrique())) {
            existingProduct.setRubrique(updatedProduct.getRubrique());
        }

        // Save and return the updated Product
        return productRepository.save(existingProduct);
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
