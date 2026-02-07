package com.webelec.client.dev;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.webelec.client.ClientEntity;
import com.webelec.repository.ClientRepository;

@Component
@Profile("dev")
public class ClientDevDataInitializer implements CommandLineRunner {

    private final ClientRepository repository;

    public ClientDevDataInitializer(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            return; // on ne touche à rien si déjà des données
        }

        List<ClientEntity> clients = List.of(
                create("Dupont", "Jean", "jean.dupont@webelec.be", "0470123456"),
                create("Martin", "Claire", "claire.martin@webelec.be", "0470987654"),
                create("Durand", "Paul", "paul.durand@webelec.be", "0466122334")
        );

        repository.saveAll(clients);
    }

    private ClientEntity create(
            String nom,
            String prenom,
            String email,
            String telephone
    ) {
        ClientEntity client = new ClientEntity();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setTelephone(telephone);
        client.setCreatedAt(Instant.now());
        return client;
    }
}