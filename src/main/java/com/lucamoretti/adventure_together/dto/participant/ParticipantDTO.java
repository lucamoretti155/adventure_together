package com.lucamoretti.adventure_together.dto.participant;

import com.lucamoretti.adventure_together.model.participant.Participant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import java.time.LocalDate;

/*
    Data Transfer Object per i partecipanti a una prenotazione.
    Include validazioni per i campi obbligatori e la data di nascita.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDTO {

    // id del participant se già persistito (null in creazione)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    private LocalDate dateOfBirth;

    public static Participant fromDTO(ParticipantDTO dto) {
        Participant p = new Participant();
        p.setId(dto.getId()); // può essere null in create
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setDateOfBirth(dto.getDateOfBirth());
        return p;
    }

    public static ParticipantDTO fromEntity(Participant entity) {
        return ParticipantDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .build();
    }
}
