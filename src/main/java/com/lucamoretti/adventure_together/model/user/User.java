package com.lucamoretti.adventure_together.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/* Classe astratta User che implementa UserDetails per l'integrazione con Spring Security
Utilizza l'ereditarietà JOINED per mappare le sottoclassi in tabelle separate
Il ruolo dell'utente è determinato dal discriminatore e mappato come enum Role
Lombok è usato per generare boilerplate code come getter, setter, costruttori Etc.
Annotazioni JPA per la mappatura della tabella e delle colonne
Implementa i metodi richiesti da UserDetails per l'autenticazione e l'autorizzazione
Il campo 'active' determina se l'account è abilitato
Il metodo getAuthorities restituisce il ruolo dell'utente basato sul discriminatore
L'email è usata come username per l'autenticazione
Altri metodi di UserDetails sono implementati per riflettere lo stato dell'account
Le sottoclassi concrete (Admin, Planner, Traveler) estenderanno questa classe e definiranno attributi specifici per ciascun tipo di utente.
Ogni sottoclasse avrà il proprio discriminatore che verrà automaticamente gestito da JPA.
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "users") // Tabella principale per tutti gli utenti
@Inheritance(strategy = InheritanceType.JOINED) // Strategia di ereditarietà JOINED
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 20) // Colonna discriminatore per il ruolo
public abstract class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // Identificatore univoco generato automaticamente
    private Long id;

    //evito di usare username, uso direttamente email come identificativo unico
    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String firstName;

    @Column(nullable=false)
    private String lastName;

    @Column(nullable=false)
    private boolean active = true;

    // Riflette il valore del discriminatore; non scrivibile direttamente
    @Column(name = "role", insertable = false, updatable = false) // Mappa il discriminatore come colonna
    private String role;

    // Implementazione di UserDetails per Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Ruolo basato sul discriminatore
        return List.of(new SimpleGrantedAuthority("ROLE_" + role)); // Prefisso "ROLE_" richiesto da Spring Security
    }
    // Spring Security usa l'email come username
    @Override public String getUsername() { return email; }
    // Altri metodi di UserDetails sempre true, tranne isEnable che si basa su attibuto 'active'
    @Override public boolean isAccountNonExpired() { return true; } // Account non scaduto
    @Override public boolean isAccountNonLocked() { return true; } // Account non bloccato
    @Override public boolean isCredentialsNonExpired() { return true; } // Credenziali non scadute
    @Override public boolean isEnabled() { return active; } // Account attivo gestito tramite 'active' campo
}
