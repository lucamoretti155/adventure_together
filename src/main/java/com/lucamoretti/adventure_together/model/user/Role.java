package com.lucamoretti.adventure_together.model.user;

// Enum per i ruoli degli utenti
// Utilizzato per definire i permessi e le funzionalità accessibili a ciascun tipo di utente
// Usato come helper per gestire i ruoli in modo chiaro e centralizzato nella logica dell'applicazione
// Non mappato come entità JPA, ma usato all'interno delle entità User e sue sottoclassi (come sorta di constanti)

public enum Role {
    ADMIN, PLANNER, TRAVELER
}