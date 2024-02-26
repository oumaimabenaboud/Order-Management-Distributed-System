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
            productRepository.save(new Product(1, "Ordinateur portable Dell XPS 15", "Puissant ordinateur portable avec processeur Intel Core i7, design fin.", "Achat de fournitures informatiques"));
            productRepository.save(new Product(2, "Kit de verrerie de laboratoire", "Ensemble complet de verrerie pour expériences scientifiques comprenant des éprouvettes, des pipettes et des béchers.", "Achat de matériel scientifique"));
            productRepository.save(new Product(3, "Matériau pour imprimante 3D", "Filament PLA de haute qualité pour l'impression 3D précise et fiable.", "Achat de matières premières"));
            productRepository.save(new Product(4, "Billets de train ONCF", "Billets de train pour les déplacements professionnels à l'intérieur du Royaume.", "Indemnités de déplacement à l'intérieur du Royaume"));
            productRepository.findAll().forEach((c->{
                System.out.println((c.toString()));
            }));
        };
    }
}
