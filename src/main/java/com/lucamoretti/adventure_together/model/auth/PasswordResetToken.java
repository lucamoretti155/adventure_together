package com.lucamoretti.adventure_together.model.auth;

import com.lucamoretti.adventure_together.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/*  Entity che rappresenta un token per il reset della password.
    Contiene il token univoco, l'utente associato e la data di scadenza del token.
    Utilizzato per gestire sia le richieste manuali di reset della password, sia per il reset obbligatorio
    per i nuovi planner e admin creati dagli admin.
*/

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}

