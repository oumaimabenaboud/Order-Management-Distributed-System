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
import org.sid.commandeservice.web.CommandeRestController;
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
	CommandLineRunner start(CommandeRepository commandeRepository, CommandeLineRepository commandeLineRepository, ProfesseurRestClient professeurRestClient, ProductRestClient productRestClient, CommandeRestController commandeRestController) {
		return args -> {
			// Generate Commande instances for the year 2023
			generateCommandes(commandeRepository, commandeLineRepository, professeurRestClient, productRestClient, commandeRestController, 2023, 5, 2L);

			// Generate Commande instances for the year 2024
			generateCommandes(commandeRepository, commandeLineRepository, professeurRestClient, productRestClient, commandeRestController, 2024, 5, 1L);
		};
	}

	private void generateCommandes(CommandeRepository commandeRepository, CommandeLineRepository commandeLineRepository, ProfesseurRestClient professeurRestClient, ProductRestClient productRestClient, CommandeRestController commandeRestController, int year, int numberOfCommandes, Long budgetId) {
		for (int i = 1; i <= numberOfCommandes; i++) { // Change the loop condition to include the last commande
			Commande commande = generateCommande(year, budgetId, productRestClient, commandeRepository, commandeLineRepository);
			commandeRestController.addCommande(commande);
		}
	}


	private Commande generateCommande(int year, Long budgetId, ProductRestClient productRestClient, CommandeRepository commandeRepository, CommandeLineRepository commandeLineRepository) {
		Commande commande = Commande.builder()
				.commandeDate(new Date(year - 1900, 0, 1)) // Set the date to January 1st of the specified year
				.profId(2L)
				.structureId(1L)
				.budgetId(budgetId)
				.type(commandestype.EN_COURS)
				.build();
		commandeRepository.save(commande);

		List<Product> products = productRestClient.getAllProducts();
		int totalHT = 0;
		List<CommandeLine> commandeLines = new ArrayList<>();
		for (Product product : products) {
			CommandeLine commandeLine = generateCommandeLine(product);
			System.out.println(commandeLine);
			totalHT += commandeLine.getQuantity() * commandeLine.getPrixHT();
			commandeLine.setCommandeId(commande.getId());
			commandeLineRepository.save(commandeLine);
			commandeLines.add(commandeLine);

		}
		System.out.println(commandeLines);
		commande.setCommandeLines(commandeLines);
		commande.setPrixTotalHT(totalHT);
		commande.setPrixTotalTTC(totalHT + totalHT * 0.20);
		return commandeRepository.save(commande);
	}

	private CommandeLine generateCommandeLine(Product product) {
		CommandeLine commandeLine = new CommandeLine();
		commandeLine.setQuantity((int) (Math.random() * 10) + 2);
		commandeLine.setPrixHT((int) (Math.random() * 10) + 2);
		commandeLine.setPrixTTC(commandeLine.getPrixHT() + commandeLine.getPrixHT() * 0.20);
		commandeLine.setProductName(product.getNom());
		commandeLine.setProductId(product.getId());
		commandeLine.setProduitRubriqueId(product.getRubriqueId());
		System.out.println(commandeLine);
		return commandeLine;
	}
}

