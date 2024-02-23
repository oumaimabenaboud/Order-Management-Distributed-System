package org.sid.productservice.web;

import lombok.AllArgsConstructor;
import org.sid.productservice.entities.Product;
import org.sid.productservice.model.Rubrique;
import org.sid.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.sid.productservice.feign.BudgetRestClient;

@RestController@AllArgsConstructor
@RequestMapping("/products")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BudgetRestClient budgetRestClient;


    @GetMapping
    public List<Product> products(){
        return  productRepository.findAll();
    }

    @GetMapping("{id}")
    public Product products(@PathVariable Long id){
        return productRepository.findById(id)
                .orElseThrow((()-> new RuntimeException(String.format("Account % not found",id))));
    }

    @GetMapping("/all")
    public List<Product> allproducts() {
        List<Product> products = productRepository.findAll();
        populateProductRubriques(products);
        return products;
    }

    private void populateProductRubriques(List<Product> products) {
        for (Product product : products) {
            // Assuming each product has a rubriqueId that corresponds to its rubrique
            Long rubriqueId = Long.parseLong(product.getRubrique());
            Rubrique fetchedRubrique = budgetRestClient.getRubriqueById(rubriqueId);
            if (fetchedRubrique != null) {
                // Assuming rubrique name is stored in the name attribute of the Rubrique object
                product.setRubrique(fetchedRubrique.getNom());
            } else {
                System.out.println("Failed to fetch rubrique with ID: " + rubriqueId);
            }
        }
    }

    @GetMapping("/search/byName")
    public Product getProductByName(@RequestParam(name="name") String name) {
        Product product = productRepository.findByNom(name);
        if (product == null) {
            throw new RuntimeException("Product not found with name: " + name);
        }
        return product;
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Product %d not found", id)));

        productRepository.delete(product);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product newProduct) {
        return productRepository.save(newProduct);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Check and update the fields of the existing Product with non-null values from the updatedProduct
        if (updatedProduct.getNom() != null) {
            existingProduct.setNom(updatedProduct.getNom());
        }
        if (updatedProduct.getDesc() != null) {
            existingProduct.setDesc(updatedProduct.getDesc());
        }
        if (updatedProduct.getRubrique() != null) {
            existingProduct.setRubrique(updatedProduct.getRubrique());
        }
        if (updatedProduct.getPrixHT() != 0) {
            existingProduct.setPrixHT(updatedProduct.getPrixHT());
        }
        if (updatedProduct.getPrixTTC() != 0) {
            existingProduct.setPrixTTC(updatedProduct.getPrixTTC());
        }

        // Save and return the updated Product
        return productRepository.save(existingProduct);
    }
}
