package org.sid.commandeservice;

import org.sid.commandeservice.entities.Commande;
import org.sid.commandeservice.entities.CommandeLine;
import org.sid.commandeservice.enums.commandestype;
import org.sid.commandeservice.feign.ProductRestClient;
import org.sid.commandeservice.feign.ProfesseurRestClient;
import org.sid.commandeservice.model.Product;
import org.sid.commandeservice.model.Professeur;
import org.sid.commandeservice.repository.CommandeRepository;
import org.sid.commandeservice.repository.CommandeLineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class CommandeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommandeServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner start(CommandeRepository commandeRepository, CommandeLineRepository commandeLineRepository, ProfesseurRestClient professeurRestClient, ProductRestClient productRestClient){
		return args -> {
			Commande commande = Commande.builder()
					.commandeDate(new Date()) // Set the current date
					.profId(1L)
					.structureId(1L)
					.type(commandestype.EN_COURS)
					.build();
			commandeRepository.save(commande);

			// Fetch products from the ProductRestClient
			List<Product> products = productRestClient.getAllProducts();
			int totalHT = 0;
			// Create CommandeLine instances based on the fetched products
			List<CommandeLine> commandeLines = new ArrayList<>();
			for (Product product : products) {
				CommandeLine commandeLine = new CommandeLine();
				int quantity = (int) (Math.random() * 10) + 1;
				int priceht= (int) (Math.random() * 10) + 1;
				int totalHTligne=quantity*priceht;
				commandeLine.setQuantity(quantity); // Set a default quantity
				commandeLine.setPrixHT(priceht); // Set total prix HT (if applicable)
				commandeLine.setPrixTTC(priceht+priceht*0.20); // Set total prix TTC (if applicable)
				commandeLine.setProductName(product.getNom());
				commandeLine.setProductId(product.getId());
				commandeLine.setProduitRubriqueId(product.getRubriqueId());
				commandeLine.setCommandeId(commande.getId()); // Set this when creating Commande
				totalHT +=totalHTligne;
				commandeLines.add(commandeLine);
			}
			commandeLineRepository.saveAll(commandeLines);
			commande.setCommandeDate(new Date()); // Set the current date
			commande.setCommandeLines(commandeLines); // Set the list of CommandeLines
			commande.setPrixTotalHT(totalHT); // Set total prix HT (if applicable)
			commande.setPrixTotalTTC(totalHT+totalHT*0.20); // Set total prix TTC (if applicable)
			// Save Commande instance
			commandeRepository.save(commande);
		};
	}


}


