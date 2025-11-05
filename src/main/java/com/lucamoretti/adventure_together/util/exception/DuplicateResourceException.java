package com.lucamoretti.adventure_together.util.exception;

/*
 Eccezione lanciata quando si tenta di creare una risorsa che già esiste.
 Ad esempio, quando si tenta di registrare un utente con un'email già in uso.
*/

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
