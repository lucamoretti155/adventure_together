package com.lucamoretti.adventure_together.dto.details;

import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DTO per rappresentare un aeroporto di partenza
 Esempio: Milano Malpensa, Roma Fiumicino
 Viene utilizzato per specificare gli aeroporti di partenza dei viaggi
 Un viaggio può avere più aeroporti di partenza associati
 Viene inizialmente popolato con i principali aeroporti, ma può essere esteso dagli admin
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartureAirportDTO {
    private Long id;

    @NotBlank(message = "Il nome dell'aeroporto è obbligatorio")
    private String name;

    @NotBlank(message = "Il codice IATA è obbligatorio")
    @Size(min = 3, max = 3, message = "Il codice IATA deve contenere 3 lettere")
    private String code;

    @NotBlank(message = "La città è obbligatoria")
    private String city;

    public static DepartureAirportDTO fromEntity(DepartureAirport entity) {
        return DepartureAirportDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .city(entity.getCity())
                .build();
    }

    public DepartureAirport toEntity() {
        DepartureAirport airport = new DepartureAirport();
        airport.setId(this.id);
        airport.setName(this.name);
        airport.setCode(this.code);
        airport.setCity(this.city);
        return airport;
    }
}

