package com.lucamoretti.adventure_together.dto.details;

import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DTO per rappresentare un paese
 Esempio: Italia, Francia, Giappone
 Viene utilizzato per specificare i paesi di destinazione dei viaggi
 Un paese è associato a una singola area geografica (GeoArea)
 Viene inizialmente popolato con i principali paesi, ma può essere esteso dagli admin
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryDTO {

    private Long id;

    @NotBlank(message = "Il nome del paese è obbligatorio")
    @Size(max = 100)
    private String country;

    @NotNull(message = "L'area geografica associata è obbligatoria")
    private Long geoAreaId;

    // utile per mostrare anche il nome dell’area nel DTO in output
    private String geoAreaName;

    public static CountryDTO fromEntity(Country entity) {
        return CountryDTO.builder()
                .id(entity.getId())
                .country(entity.getCountry())
                .geoAreaId(
                        entity.getGeoArea() != null ? entity.getGeoArea().getId() : null
                )
                .geoAreaName(
                        entity.getGeoArea() != null ? entity.getGeoArea().getGeoArea() : null
                )
                .build();
    }

    /*
     * Converte il DTO in entità Country.
     * Richiede che la GeoArea esista già: viene passato un oggetto GeoArea
     * recuperato dal repository nel Service.
     */
    public Country toEntity(GeoArea geoArea) {
        Country entity = new Country();
        entity.setId(this.id);
        entity.setCountry(this.country);
        entity.setGeoArea(geoArea);
        return entity;
    }
}

