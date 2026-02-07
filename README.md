# Webelec v1 - Module Client

## Vue d'ensemble
Ce module expose un mini-API REST pour gerer des clients (creation et recherche/pagination).
Le modele persiste est base sur `ClientEntity` et la table `client`.

## Structure des classes

- `com.webelec.client.ClientEntity`
  - Entite JPA mappee sur la table `client`.
  - Champs: `id` (UUID), `nom`, `prenom`, `email`, `telephone`, `createdAt`.
  - `@PrePersist` renseigne `createdAt` si absent.

- `com.webelec.repository.ClientRepository`
  - Repository Spring Data JPA pour `ClientEntity`.
  - Fournit `findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(...)`.

- `com.webelec.client.service.ClientService`
  - Couche service: creation, lecture, mise a jour, suppression et recherche paginee.
  - Mappe `ClientEntity` vers `ClientResponse`.

- `com.webelec.client.dto.ClientCreateRequest`
  - DTO d'entree pour la creation.
  - Validation: nom/prenom obligatoires, email au format valide, telephone limite.

- `com.webelec.client.dto.ClientUpdateRequest`
  - DTO d'entree pour la mise a jour.
  - Validation identique a la creation.

- `com.webelec.client.dto.ClientResponse`
  - DTO de sortie expose par l'API.

- `com.webelec.client.api.ClientController`
  - Controleur REST expose sous `/api/clients`.
  - `GET /api/clients`: recherche et pagination (param `search`).
  - `GET /api/clients/{id}`: recuperation d'un client.
  - `POST /api/clients`: creation d'un client.
  - `PUT /api/clients/{id}`: mise a jour d'un client.
  - `DELETE /api/clients/{id}`: suppression d'un client.

- `com.webelec.client.dev.ClientDevDataInitializer`
  - Donnees de dev (profil `dev`).
  - Insere quelques clients si la table est vide.

## Fonctionnement

1) Creation
- Endpoint: `POST /api/clients`
- Corps JSON: `nom`, `prenom`, `email`, `telephone`.
- Validation cote serveur via `@Valid`.
- Retour: `ClientResponse`.

2) Lecture
- Endpoint: `GET /api/clients/{id}`
- Retour: `ClientResponse`.

3) Recherche / pagination
- Endpoint: `GET /api/clients`
- Parametres optionnels:
  - `search`: filtre sur `nom`, `prenom` ou `email`.
  - `page`, `size`, `sort`: pagination Spring Data.
- Retour: page de `ClientResponse`.

4) Mise a jour
- Endpoint: `PUT /api/clients/{id}`
- Corps JSON: `nom`, `prenom`, `email`, `telephone`.
- Retour: `ClientResponse`.

5) Suppression
- Endpoint: `DELETE /api/clients/{id}`
- Retour: `204 No Content`.

## Notes d'alignement
- Le modele persiste est uniquement `ClientEntity`.
- Les anciens types dans `com.webelec.domain.client` ont ete supprimes pour eviter les collisions JPA.

## Tests
- `ClientControllerIT` couvre les tris multi-champs et les cas d'erreur de tri.

## Commandes utiles

```bat
cd /d C:\Users\chris\eclipse-workspace\webelec-v1
mvnw -q -Dtest=ClientControllerIT test
```