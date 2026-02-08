package com.webelec.client.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.webelec.client.dto.ClientCreateRequest;
import com.webelec.client.dto.ClientResponse;
import com.webelec.client.dto.ClientUpdateRequest;
import com.webelec.client.service.ClientService;

import jakarta.validation.Valid;

/**
 * Controleur REST pour la gestion des clients.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Recherche paginee des clients.
     *
     * @param search filtre optionnel
     * @param pageable pagination et tri
     * @return page de clients
     */
    @GetMapping
    public Page<ClientResponse> search(@RequestParam(required = false) String search, Pageable pageable) {
        return clientService.search(search, pageable);
    }

    /**
     * Retourne un client par identifiant.
     *
     * @param id identifiant du client
     * @return client
     */
    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable UUID id) {
        return clientService.getById(id);
    }

    /**
     * Cree un nouveau client.
     *
     * @param request donnees de creation
     * @return client cree
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(@Valid @RequestBody ClientCreateRequest request) {
        return clientService.create(request);
    }

    /**
     * Met a jour un client existant.
     *
     * @param id identifiant du client
     * @param request donnees de mise a jour
     * @return client mis a jour
     */
    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable UUID id, @Valid @RequestBody ClientUpdateRequest request) {
        return clientService.update(id, request);
    }

    /**
     * Supprime un client.
     *
     * @param id identifiant du client
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        clientService.delete(id);
    }
}