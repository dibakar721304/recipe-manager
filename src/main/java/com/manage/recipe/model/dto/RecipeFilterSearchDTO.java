package com.manage.recipe.model.dto;

import com.manage.recipe.model.FoodCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFilterSearchDTO {
    private String name;
    private String foodCategory;
    private Integer servings;
    private List<String> includedIngredients;
    private List<String> excludedIngredients;
    private String searchTextInInstructions;

    public FoodCategory getFoodCategoryEnum(String veg) {
        try {
            return FoodCategory.valueOf(foodCategory.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return null;
        }
    }
}
