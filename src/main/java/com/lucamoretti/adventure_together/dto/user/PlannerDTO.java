package com.lucamoretti.adventure_together.dto.user;

import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.model.user.Traveler;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

// Data Transfer Object (DTO) per l'entità Planner
// Estende UserDTO aggiungendo attributi specifici per i planner
// Utilizzato per trasferire i dati dei viaggiatori tra client e server

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDTO extends UserDTO {
    @NotBlank(message = "Employee ID non può essere vuoto")
    private Long employeeId;

    // Costruttore con builder personalizzato per PlannerDTO
    // UserDTO ha già un builder, quindi qui ne definiamo uno specifico per TravelerDTO
    // il builer di UserDTO non può essere ereditato
    @Builder(builderMethodName = "plannerBuilder")
    public PlannerDTO(Long id, String email, String firstName, String lastName,
                       boolean active, String role,
                       Long employeeId) {
        super(id, email, firstName, lastName, active, role);
        this.employeeId = employeeId;
    }

    // Converte un'entità Planner in un DTO PlannerDTO
    public static PlannerDTO fromEntity(Planner planner) {
        return PlannerDTO.plannerBuilder()
                .id(planner.getId())
                .email(planner.getEmail())
                .firstName(planner.getFirstName())
                .lastName(planner.getLastName())
                .active(planner.isEnabled())
                .role(planner.getRole())
                .employeeId(planner.getEmployeeId())
                .build();
    }
    // Converte questo DTO PlannerDTO in un'entità Planner
    // E' necessario per creare o aggiornare un Planner nel database
    // Riutilizza il metodo della superclasse UserDTO ad eccezione degli attributi specifici
    public Planner toEntity() {
        Planner planner = new Planner();
        populateEntity(planner); // riutilizza il metodo della superclasse UserDTO
        planner.setEmployeeId(employeeId);
        return planner;
    }
}
