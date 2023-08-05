package com.manage.recipe.model.dto;

import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long recipeId;

    @NotBlank(message = "Recipe name can not be null/empty")
    private String name;

    private FoodCategory foodCategory;

    @Min(value = 1, message = "There should be at least 1 serving number")
    private int servings;

    private List<IngredientDAO> ingredients;
    private String instructions;
}
