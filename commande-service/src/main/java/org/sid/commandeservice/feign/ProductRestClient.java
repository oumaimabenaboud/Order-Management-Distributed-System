package org.sid.commandeservice.feign;

import org.sid.commandeservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="PRODUCT-SERVICE")
public interface ProductRestClient {
    @GetMapping(path="/products")
    List<Product> getAllProducts();
    //@RequestParam(name="page") int page, @RequestParam(name="size")  int size
    @GetMapping(path="/products/{id}")
    Product getProductById (@PathVariable(name="id")  Long id);

}
