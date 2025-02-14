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
            ProfesseurRepo.save(new professeur(null, "admin", "admin","admin", passwordEncoder.encode("admin"),false,true));
            ProfesseurRepo.save(new professeur(null, "Bekri", "My.Ali","a.bekri@umi.ac.ma", passwordEncoder.encode("bekri_ali"),true,false));
            ProfesseurRepo.save(new professeur(null, "Oubelkacem", "Ali","a.oubelkacem@umi.ac.ma", passwordEncoder.encode("oubelkacem_ali"),true,false));
            ProfesseurRepo.save(new professeur(null, "Bourray", "Hamid","h.bourray@umi.ac.ma", passwordEncoder.encode("bourray_hamid"),true,false));
            ProfesseurRepo.save(new professeur(null, "Alaoui Ismaili", "Mehdi","m.alaouiismaili@umi.ac.ma", passwordEncoder.encode("alaoui_ismaili_mehdi"),true,false));
            ProfesseurRepo.save(new professeur(null, "Zitane", "Mohammed","m.zitane@umi.ac.ma", passwordEncoder.encode("zitane_mohammed"),true,false));
            ProfesseurRepo.findAll().forEach(c ->{
                        System.out.println(c.toString());
                    }
            );

        };
    }

}
