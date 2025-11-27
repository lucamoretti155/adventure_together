package com.lucamoretti.adventure_together.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 Eccezione lanciata quando un'azione non è autorizzata per il ruolo dell'utente.
 Restituisce uno status HTTP 403 Forbidden.
 Esempio di utilizzo:
 - un Traveler tenta di lasciare una review per un viaggio a cui non ha partecipato.
 */

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UnauthorizedActionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedActionException(String message) {
        super(message);
    }

    public UnauthorizedActionException(String action, String role) {
        super(String.format("Azione '%s' non permessa per il ruolo '%s'", action, role));
    }
}

