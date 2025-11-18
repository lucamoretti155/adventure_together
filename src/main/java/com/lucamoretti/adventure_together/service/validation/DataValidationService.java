package com.lucamoretti.adventure_together.service.validation;

import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;

/*
  Servizio per la validazione dei dati relativi a viaggiatori e viaggi.
  Fornisce metodi per validare età, password, date dei viaggi e numero di partecipanti.
 */

@Service
public class DataValidationService {
    // valida i dati di un viaggiatore (età e password) usando i metodi specifici
    public void validateTraveler(TravelerDTO dto, String rawPassword) {
        validatePassword(rawPassword);
        validateAdultAge(dto.getDateOfBirth());
    }

    // valida che il viaggiatore sia maggiorenne (18 anni o più)
    public void validateAdultAge(LocalDate dateOfBirth) {
        if (calculateAge(dateOfBirth) < 18) {
            throw new DataIntegrityException("Per registrarti devi essere maggiorenne");
        }
    }
    // valida la forza della password secondo i criteri specificati
    // e.g. "Password1!"
    public void validatePassword(String password) {
        if (!isPasswordStrong(password)) {
            throw new DataIntegrityException("La password deve includere almeno una maiuscola, una minuscola, un numero, un simbolo e deve essere lunga almeno 8 caratteri");
        }
    }
    // valida la coerenza delle date di inizio e fine di un viaggio
    public void validateTripDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new DataIntegrityException("Data di fine non può essere precedente alla data di inizio");
        }
    }
    // min e max devono essere entrambi positivi e min non deve essere maggiore di max
    // usato quando viene creato un tripItinerary e viene specificato il numero minimo e massimo di partecipanti
    public void validateParticipants(int min, int max) {
        if (min <= 0 || max <= 0 || min > max) {
            throw new DataIntegrityException("Numero di partecipanti non valido");
        }
    }

    // Helper per calcolare l'età in anni a partire dalla data di nascita
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    // Helper per verificare la forza della password usando una regex
    private boolean isPasswordStrong(String password) {
        // almeno una maiuscola, una minuscola, un numero, un simbolo e almeno 8 caratteri (e massimo 20)
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,20}$");
    }
}

