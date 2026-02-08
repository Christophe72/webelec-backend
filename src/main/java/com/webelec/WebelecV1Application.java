package com.webelec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree Spring Boot de l'application Webelec.
 */
@SpringBootApplication
public class WebelecV1Application {

    /**
     * Demarre l'application Spring Boot.
     *
     * @param args arguments de ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(WebelecV1Application.class, args);
    }

}