package com.lucamoretti.adventure_together.util.exception;

/*
 Eccezione lanciata quando si verifica un errore durante l'operazione di archiviazione di un file.
 Restituisce un messaggio di errore personalizzato.
 */

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
}
