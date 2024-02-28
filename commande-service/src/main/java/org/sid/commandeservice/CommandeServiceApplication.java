package org.sid.commandeservice;

import org.sid.commandeservice.entities.Commande;
import org.sid.commandeservice.entities.CommandeLine;
import org.sid.commandeservice.enums.commandestype;
import org.sid.commandeservice.feign.ProductRestClient;
import org.sid.commandeservice.feign.ProfesseurRestClient;
import org.sid.commandeservice.model.Budget;
import org.sid.commandeservice.model.Product;
import org.sid.commandeservice.model.Professeur;
import org.sid.commandeservice.model.RubriqueAllocation;
import org.sid.commandeservice.repository.CommandeRepository;
import org.sid.commandeservice.repository.CommandeLineRepository;
import org.sid.commandeservice.feign.BudgetRestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
	CommandLineRunner start(CommandeRepository commandeRepository, CommandeLineRepository commandeLineRepository, ProfesseurRestClient professeurRestClient, ProductRestClient productRestClient, BudgetRestClient budgetRestClient) {
		return args -> {
			// Generate Commande instances for the year 2023
			for (int i = 0; i < 5; i++) { // Generate 5 commandes for the year 2023
				 generateAndSaveCommande(commandeRepository, commandeLineRepository, professeurRestClient, productRestClient, budgetRestClient, 2023, 2L, commandestype.LIVRÉE);
			}

			// Generate Commande instances for the year 2024
			for (int i = 0; i < 5; i++) { // Generate 5 commandes for the year 2024
				generateAndSaveCommande(commandeRepository, commandeLineRepository, professeurRestClient, productRestClient, budgetRestClient, 2024, 1L, commandestype.EN_COURS);
			}
		};
	}

	private void generateAndSaveCommande(CommandeRepository commandeRepository, CommandeLineRepository commandeLineRepository, ProfesseurRestClient professeurRestClient, ProductRestClient productRestClient, BudgetRestClient budgetRestClient, int year , Long BudgetId, commandestype type) {
		LocalDate localDate = LocalDate.of(year, 1, 1);
		// Convert LocalDate to Date object
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		System.out.println(date);

		Commande commande = Commande.builder()
				.commandeDate(date)
				.profId(2L)
				.structureId(1L)
				.budgetId(BudgetId)
				.type(type)
				.build();
		Commande newCommand = commandeRepository.save(commande);

		List<Product> products = productRestClient.getAllProducts();
		int totalHT = 0;
		List<CommandeLine> commandeLines = new ArrayList<>();
		for (Product product : products) {
			CommandeLine commandeLine = new CommandeLine();
			int quantity = (int) (Math.random() * 10) + 1;
			System.out.println(quantity);
			int priceht = (int) (Math.random() * 10) + 1;
			System.out.println(priceht);
			int totalHTligne = quantity * priceht;
			commandeLine.setQuantity(quantity);
			commandeLine.setPrixHT(priceht);
			commandeLine.setPrixTTC(priceht + priceht * 0.20);
			commandeLine.setProductName(product.getNom());
			commandeLine.setProductId(product.getId());
			commandeLine.setProduitRubriqueId(product.getRubriqueId());
			commandeLine.setCommandeId(newCommand.getId());
			totalHT += totalHTligne;
			commandeLines.add(commandeLine);
		}
		commandeLineRepository.saveAll(commandeLines);
		newCommand.setCommandeLines(commandeLines);
		newCommand.setPrixTotalHT(totalHT);
		newCommand.setPrixTotalTTC(totalHT + totalHT * 0.20);
		commandeRepository.save(newCommand);

		// Update budget after saving Commande
		Budget budget = budgetRestClient.getBudgetById(BudgetId);
		List<RubriqueAllocation> rubriquesAllocations = budget.getRubriqueAllocations();
		for(CommandeLine commandeLine : commandeLines){
			for(RubriqueAllocation rubriqueAllocation : rubriquesAllocations){
				if (rubriqueAllocation.getRubriqueId()== commandeLine.getProduitRubriqueId()){
					if (commandeLine.getQuantity()<= 0 || commandeLine.getPrixHT()<= 0 || commandeLine.getPrixTTC()<= 0){
						// Handle invalid command line data
						throw new RuntimeException("Les valeurs de la quantité, du prix hors taxe (HT) ou du prix toutes taxes comprises (TTC) doivent être supérieures à zéro.");
					}
					if (commandeLine.getProductId() == null ){
						// Handle missing product ID
						throw new RuntimeException("Veuillez choisir un produit parmi la liste des produits disponibles !");
					}
					if(rubriqueAllocation.getMontantRestant()<commandeLine.getPrixTTC()*commandeLine.getQuantity()){
						// Handle exceeded budget allocation
						throw new RuntimeException("Vous avez dépassé le montant alloué à la rubrique :"+ rubriqueAllocation.getRubriqueName());
					}else{
						rubriqueAllocation.setMontantRestant(Math.round((rubriqueAllocation.getMontantRestant() - commandeLine.getPrixTTC() * commandeLine.getQuantity()) * 100.0) / 100.0);
						commandeLineRepository.save(commandeLine);
					}
				}
			}
		}

		if(commandeLines.isEmpty()){
			// Delete command if no products detected
			commandeRepository.deleteById(newCommand.getId());
			throw new RuntimeException("Aucun produit n'a été détecté dans cette commande. La commande est vide.");
		}
			budgetRestClient.updateAllocations(BudgetId, rubriquesAllocations);
		}

}