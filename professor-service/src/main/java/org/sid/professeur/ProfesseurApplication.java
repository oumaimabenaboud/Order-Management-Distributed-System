package org.sid.professeur;

import org.sid.professeur.repositories.ProfesseurRepo;
import org.sid.professeur.entities.professeur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ProfesseurApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfesseurApplication.class, args);
    }
    @Bean
    CommandLineRunner start(ProfesseurRepo ProfesseurRepo, RepositoryRestConfiguration RepoConfig, BCryptPasswordEncoder passwordEncoder){
        RepoConfig.exposeIdsFor(professeur.class);
        return args -> {
            ProfesseurRepo.save(new professeur(null, "Bekri", "Ali","a.bikri@umi.ac.ma", passwordEncoder.encode("4444"),true));
            ProfesseurRepo.save(new professeur(null, "Oubelkacem", "Ali","a.oubelkacem@umi.ac.ma", passwordEncoder.encode("4555"),true));
            ProfesseurRepo.save(new professeur(null, "9ezzouz", "Jalal","jalal@umi.ac.ma", passwordEncoder.encode("0000"),false));
            ProfesseurRepo.findAll().forEach(c ->{
                        System.out.println(c.toString());
                    }

            );
        };
    }

}
