package com.lucamoretti.adventure_together.repository.auth;

import com.lucamoretti.adventure_together.model.auth.PasswordResetToken;
import com.lucamoretti.adventure_together.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository per la gestione dei token di reset della password.
// Fornisce metodi per trovare un token per il reset della password in base al token stesso
// e per eliminare i token associati a un utente specifico.

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
