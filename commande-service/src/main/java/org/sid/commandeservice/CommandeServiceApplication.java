package org.sid.commandeservice;

import org.sid.commandeservice.entities.Commande;
import org.sid.commandeservice.entities.CommandeLine;
import org.sid.commandeservice.enums.commandestype;
import org.sid.commandeservice.feign.ProductRestClient;
import org.sid.commandeservice.feign.ProfesseurRestClient;
import org.sid.commandeservice.model.Professeur;
import org.sid.commandeservice.repository.CommandeRepository;
import org.sid.commandeservice.repository.CommandeLineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

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
	CommandLineRunner start(CommandeRepository commandeRepository, CommandeLineRepository productItemRepository, ProfesseurRestClient professeurRestClient, ProductRestClient productRestClient){
		return args -> {

			Professeur professeur = professeurRestClient.getProfesseurById(1L);
			Commande commande1 = commandeRepository.save(new Commande(null,new Date(),null, professeur.getId(),null,0.0,0.0,commandestype.encours));
			double totalHT = 0.0;
			double totalTTC = 0.0;
			List<org.sid.commandeservice.model.Product> productPagedModel = productRestClient.products();
			for (org.sid.commandeservice.model.Product p : productPagedModel) {
				CommandeLine commandeLine =new CommandeLine();
				commandeLine.setQuantity(1+new Random().nextInt(100));
				commandeLine. setTotal_prixHT_ligne(p.getPrixHT()* commandeLine.getQuantity());
				commandeLine. setTotal_prixTTC_ligne(p.getPrixTTC( )* commandeLine.getQuantity());
				commandeLine.setCommande(commande1);
				commandeLine.setId(p.getId());
				productItemRepository.save(commandeLine);
				totalHT += commandeLine.getTotal_prixHT_ligne();
				totalTTC += commandeLine.getTotal_prixTTC_ligne();
					}
			commande1.setPrix_total_HT(totalHT);
			commande1.setPrix_total_TTC(totalTTC);
			commandeRepository.save(commande1);

			};

		}

}
