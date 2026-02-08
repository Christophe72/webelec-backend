package com.webelec.repository;

import com.webelec.client.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Acces aux donnees client avec recherche textuelle.
 */
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    /**
     * Recherche par nom, prenom ou email (insensible a la casse).
     *
     * @param nom valeur du nom a filtrer
     * @param prenom valeur du prenom a filtrer
     * @param email valeur de l'email a filtrer
     * @param pageable pagination et tri
     * @return page de clients correspondants
     */
    Page<ClientEntity> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom,
            String prenom,
            String email,
            Pageable pageable
    );
}