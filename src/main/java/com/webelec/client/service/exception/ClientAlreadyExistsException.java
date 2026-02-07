package com.webelec.client.service.exception;

public class ClientAlreadyExistsException extends RuntimeException {

    public ClientAlreadyExistsException(String email) {
        super("Un client avec l'email '" + email + "' existe déjà");
    }
}
