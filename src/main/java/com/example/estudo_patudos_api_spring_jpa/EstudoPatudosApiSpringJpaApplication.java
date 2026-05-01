package com.example.estudo_patudos_api_spring_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EstudoPatudosApiSpringJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstudoPatudosApiSpringJpaApplication.class, args);
    }
}
