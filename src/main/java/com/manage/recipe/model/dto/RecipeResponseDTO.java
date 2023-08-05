package com.manage.recipe.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDTO {
    private int status;

    @JsonProperty(value = "recipes")
    private List<RecipeDTO> recipeDTOList;
}
