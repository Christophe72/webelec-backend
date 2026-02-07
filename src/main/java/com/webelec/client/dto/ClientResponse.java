package com.webelec.client.dto;

import java.time.Instant;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String nom,
        String prenom,
        String email,
        String telephone,
        Instant createdAt
) {
}
