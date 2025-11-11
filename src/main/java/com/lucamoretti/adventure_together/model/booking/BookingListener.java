package com.lucamoretti.adventure_together.model.booking;

/* Interfaccia per il listener delle prenotazioni.
   Definisce il metodo update che viene chiamato per notificare cambiamenti relativi allo stato del Trip (TripState) connesso alla prenotazione,
   ad esempio per l'invio di email di Conferma del Trip.
*/

public interface BookingListener {
    void update(String mailTemplatePath);
}

