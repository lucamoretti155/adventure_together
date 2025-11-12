package com.lucamoretti.adventure_together.dto.details;

import com.lucamoretti.adventure_together.model.details.GeoArea;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DTO per il trasferimento dei dati relativi alle aree geografiche
 Contiene un identificativo univoco e il nome dell'area geografica
 Utilizzato per inviare e ricevere informazioni sulle geoArea
 L'admin può creare nuove geoArea tramite questo DTO
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoAreaDTO {
    private Long id;

    @NotBlank(message = "Il nome dell'area geografica è obbligatorio")
    private String geoArea;

    public static GeoAreaDTO fromEntity(GeoArea entity) {
        return GeoAreaDTO.builder()
                .id(entity.getId())
                .geoArea(entity.getGeoArea())
                .build();
    }

    public GeoArea toEntity() {
        GeoArea area = new GeoArea();
        area.setId(this.id);
        area.setGeoArea(this.geoArea);
        return area;
    }
}

