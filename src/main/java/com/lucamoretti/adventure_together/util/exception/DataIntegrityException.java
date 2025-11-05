package com.lucamoretti.adventure_together.util.exception;

/* Eccezione personalizzata per indicare violazioni dell'integrità dei dati
   Utilizzata nei servizi di validazione per segnalare errori nei dati forniti (e.g. età non valida, password debole, date incoerenti)
*/

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }
}
