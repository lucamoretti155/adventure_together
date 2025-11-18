package com.lucamoretti.adventure_together.controller.advice;

import com.lucamoretti.adventure_together.util.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/*
  Gestore globale delle eccezioni per l'applicazione.
  Intercetta eccezioni specifiche e restituisce risposte HTTP appropriate.
*/

@ControllerAdvice
public class GlobalExceptionHandler {
    // Gestisce le eccezioni di integrità dei dati
    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Gestisce le eccezioni di archiviazione file
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<String> handleFileStorage(FileStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    // Gestisce le eccezioni di azioni non autorizzate
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<String> handleUnauthorizedAction(UnauthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // Gestisce le eccezioni di argomenti non validi
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Gestisce le eccezioni di risorse non trovate
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Gestisce le eccezioni di risorse duplicate
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    // Gestisce le eccezioni di validazione degli argomenti dei metodi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
