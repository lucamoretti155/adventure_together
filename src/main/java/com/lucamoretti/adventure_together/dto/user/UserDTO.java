package com.lucamoretti.adventure_together.dto.user;

import com.lucamoretti.adventure_together.model.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Data Transfer Object (DTO) per l'entità User
// Contiene le informazioni essenziali dell'utente da trasferire tra client e server.
// non include informazioni sensibili come la password
// email è usata come identificatore univoco (come un username)

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank(message = "Email non può essere vuota")
    private String email;
    @NotBlank(message = "Nome non può essere vuoto")
    private String firstName;
    @NotBlank(message = "Cognome non può essere vuoto")
    private String lastName;
    private boolean active;
    private String role;

    // Converte un'entità User in un DTO UserDTO
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .role(user.getRole())
                .build();
    }

    // Helpers per popolare un'entità User con i dati di questo DTO
    // Riutilizzato nelle sottoclassi DTO
    protected void populateEntity(User user) {
        user.setId(getId());
        user.setEmail(getEmail());
        user.setFirstName(getFirstName());
        user.setLastName(getLastName());
        user.setActive(isActive());
        user.setRole(getRole());
    }


}

