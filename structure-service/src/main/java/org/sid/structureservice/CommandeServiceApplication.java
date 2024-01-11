package org.sid.structureservice;

import org.sid.structureservice.entities.Structure;
import org.sid.structureservice.entities.ResponsableStructure;
import org.sid.structureservice.enums.structurestype;
import org.sid.structureservice.feign.BudgetRestClient;
import org.sid.structureservice.feign.ProfesseurRestClient;
import org.sid.structureservice.model.Budget;
import org.sid.structureservice.model.Professeur;
import org.sid.structureservice.repository.StructureRepository;
import org.sid.structureservice.repository.ResponsableStructureRepository;
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
	CommandLineRunner start(StructureRepository structureRepository, ResponsableStructureRepository productItemRepository, ProfesseurRestClient professeurRestClient, BudgetRestClient budgetRestClient){
		return args -> {



}
