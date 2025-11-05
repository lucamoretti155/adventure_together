package com.lucamoretti.adventure_together.dto.user;

import com.lucamoretti.adventure_together.model.user.Traveler;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDate;

// Data Transfer Object (DTO) per l'entità Traveler
// Estende UserDTO aggiungendo attributi specifici per i viaggiatori
// Utilizzato per trasferire i dati dei viaggiatori tra client e server

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TravelerDTO extends UserDTO {
    @NotNull(message = "Data di nascita non può essere nulla")
    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate dateOfBirth;
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Numero di telefono non valido") // Esempi di formati validi: +1234567890, (123) 456-7890 123-4567
    private String telephone;

    // Costruttore con builder personalizzato per TravelerDTO
    // UserDTO ha già un builder, quindi qui ne definiamo uno specifico per TravelerDTO
    // il builer di UserDTO non può essere ereditato
    @Builder(builderMethodName = "travelerBuilder")
    public TravelerDTO(Long id, String email, String firstName, String lastName,
                       boolean active, String role,
                       LocalDate dateOfBirth, String telephone) {
        super(id, email, firstName, lastName, active, role);
        this.dateOfBirth = dateOfBirth;
        this.telephone = telephone;
    }

    // Converte un'entità Traveler in un DTO TravelerDTO
    public static TravelerDTO fromEntity(Traveler traveler) {
        return TravelerDTO.travelerBuilder()
                .id(traveler.getId())
                .email(traveler.getEmail())
                .firstName(traveler.getFirstName())
                .lastName(traveler.getLastName())
                .active(traveler.isEnabled())
                .role(traveler.getRole())
                .dateOfBirth(traveler.getDateOfBirth())
                .telephone(traveler.getTelephone())
                .build();
    }
    // Converte questo DTO TravelerDTO in un'entità Traveler
    // E' necessario per creare o aggiornare un Traveler nel database
    // Riutilizza il metodo della superclasse UserDTO ad eccezione degli attributi specifici
    public Traveler toEntity() {
        Traveler traveler = new Traveler();
        populateEntity(traveler); // riutilizza il metodo della superclasse UserDTO
        traveler.setDateOfBirth(dateOfBirth);
        traveler.setTelephone(telephone);
        return traveler;
    }
}


