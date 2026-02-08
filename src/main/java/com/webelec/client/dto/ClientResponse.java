package com.webelec.client.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de reponse expose pour un client.
 *
 * @param id identifiant du client
 * @param nom nom du client
 * @param prenom prenom du client
 * @param email email du client
 * @param telephone telephone du client
 * @param createdAt date de creation
 */
public record ClientResponse(
        UUID id,
        String nom,
        String prenom,
        String email,
        String telephone,
        Instant createdAt
) {
}