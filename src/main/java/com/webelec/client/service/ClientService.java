package com.webelec.client.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.webelec.client.ClientEntity;
import com.webelec.client.dto.ClientCreateRequest;
import com.webelec.client.dto.ClientResponse;
import com.webelec.client.dto.ClientUpdateRequest;
import com.webelec.repository.ClientRepository;

@Service
@Transactional
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public ClientResponse create(ClientCreateRequest request) {
        ClientEntity client = new ClientEntity();
        client.setNom(request.nom());
        client.setPrenom(request.prenom());
        client.setEmail(request.email());
        client.setTelephone(request.telephone());

        ClientEntity saved = repository.save(client);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> search(String query, Pageable pageable) {
        Page<ClientEntity> page;

        if (query == null || query.isBlank()) {
            page = repository.findAll(pageable);
        } else {
            page = repository
                    .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            query, query, query, pageable
                    );
        }

        return page.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse getById(UUID id) {
        ClientEntity client = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
        return mapToResponse(client);
    }

    public ClientResponse update(UUID id, ClientUpdateRequest request) {
        ClientEntity client = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));

        client.setNom(request.nom());
        client.setPrenom(request.prenom());
        client.setEmail(request.email());
        client.setTelephone(request.telephone());

        ClientEntity saved = repository.save(client);
        return mapToResponse(saved);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable");
        }
        repository.deleteById(id);
    }

    private ClientResponse mapToResponse(ClientEntity client) {
        return new ClientResponse(
                client.getId(),
                client.getNom(),
                client.getPrenom(),
                client.getEmail(),
                client.getTelephone(),
                client.getCreatedAt()
        );
    }
}