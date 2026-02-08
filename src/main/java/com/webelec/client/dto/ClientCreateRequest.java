package com.webelec.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Donnees de creation d'un client.
 *
 * @param nom nom du client
 * @param prenom prenom du client
 * @param email email du client
 * @param telephone telephone du client
 */
public record ClientCreateRequest(

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,

        @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
        String telephone
) {}