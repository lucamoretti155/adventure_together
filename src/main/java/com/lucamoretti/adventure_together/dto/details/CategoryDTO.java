package com.lucamoretti.adventure_together.dto.details;

import com.lucamoretti.adventure_together.model.details.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DTO per rappresentare una categoria di viaggio
 Esempio: Avventura, Cultura, Relax
 Viene utilizzato per categorizzare i viaggi in base al tipo di esperienza
 Un viaggio può appartenere a più categorie, e una categoria può includere più viaggi
 Viene aggiornata solo dagli admin per aggiungere nuove categorie di viaggio
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Il nome della categoria è obbligatorio")
    private String name;


    public static CategoryDTO fromEntity(Category entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        return category;
    }
}
