package com.manage.recipe.model.dto;

import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long recipeId;
    private String name;
    private FoodCategory foodCategory;
    private int servings;
    private List<IngredientDAO> ingredients;
    private String instructions;
}
