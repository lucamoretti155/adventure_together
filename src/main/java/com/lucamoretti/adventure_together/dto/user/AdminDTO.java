package com.lucamoretti.adventure_together.dto.user;

import com.lucamoretti.adventure_together.model.user.Admin;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.model.user.Traveler;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

// Data Transfer Object (DTO) per l'entità Admin
// Estende UserDTO aggiungendo attributi specifici per gli admin
// Utilizzato per trasferire i dati dei viaggiatori tra client e server

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO extends UserDTO {
    @NotBlank(message = "Employee ID non può essere vuoto")
    private Long employeeId;

    // Costruttore con builder personalizzato per AdminDTO
    // UserDTO ha già un builder, quindi qui ne definiamo uno specifico per TravelerDTO
    // il builer di UserDTO non può essere ereditato
    @Builder(builderMethodName = "adminBuilder")
    public AdminDTO(Long id, String email, String firstName, String lastName,
                      boolean active, String role,
                      Long employeeId) {
        super(id, email, firstName, lastName, active, role);
        this.employeeId = employeeId;
    }

    // Converte un'entità Admin in un DTO AdminDTO
    public static AdminDTO fromEntity(Admin admin) {
        return AdminDTO.adminBuilder()
                .id(admin.getId())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .active(admin.isEnabled())
                .role(admin.getRole())
                .employeeId(admin.getEmployeeId())
                .build();
    }
    // Converte questo DTO AdminDTO in un'entità Admin
    // E' necessario per creare o aggiornare un Admin nel database
    // Riutilizza il metodo della superclasse UserDTO ad eccezione degli attributi specifici
    public Admin toEntity() {
        Admin admin = new Admin();
        populateEntity(admin); // riutilizza il metodo della superclasse UserDTO
        admin.setEmployeeId(employeeId);
        return admin;
    }
}
