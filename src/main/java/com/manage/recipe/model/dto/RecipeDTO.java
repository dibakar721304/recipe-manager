package com.manage.recipe.model.dto;

import java.util.List;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private String name;
    private boolean isVegetarian;
    private int servings;
    private List<String> ingredients;
    private String instructions;
}
