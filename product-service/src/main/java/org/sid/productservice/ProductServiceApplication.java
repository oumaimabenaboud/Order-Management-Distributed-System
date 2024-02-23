package org.sid.productservice;

import org.sid.productservice.entities.Product;
import org.sid.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@EnableFeignClients
@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration restConfiguration){
        restConfiguration.exposeIdsFor(Product.class);
        return args -> {
            productRepository.save(new Product(1, "Ordinateur portable Dell XPS 15", "Puissant ordinateur portable avec processeur Intel Core i7, design fin.", "Matériel Informatique", 8000, 9600));
            productRepository.save(new Product(2, "Kit de verrerie de laboratoire", "Ensemble complet de verrerie pour expériences scientifiques comprenant des éprouvettes, des pipettes et des béchers.", "Équipement de laboratoire", 500, 600));
            productRepository.save(new Product(3, "Stylos à encre gel Pilot G2", "Stylos à encre gel de haute qualité pour une écriture fluide et précise.", "Fournitures de bureau", 10, 11));
            productRepository.save(new Product(4, "Tables de bureau ajustables", "Tables réglables en hauteur pour offrir une flexibilité ergonomique.", "Mobilier", 1000, 1200));
            productRepository.findAll().forEach((c->{
                System.out.println((c.toString()));
            }));
        };
    }
}
