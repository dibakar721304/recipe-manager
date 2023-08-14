package com.manage.recipe.model.dto;

import com.manage.recipe.model.FoodCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFilterSearchDTO {
    private static final Logger logger = LoggerFactory.getLogger(RecipeFilterSearchDTO.class);

    private String name;
    private String foodCategory;
    private Integer servings;
    private List<String> includedIngredients;
    private List<String> excludedIngredients;
    private String searchTextInInstructions;

    public FoodCategory getFoodCategoryEnum() {
        try {
            return foodCategory != null ? FoodCategory.valueOf(foodCategory.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            logger.error("Exception occured while returning food category {}", e.getMessage());
        }
        return null;
    }
}
