package com.manage.recipe.util;

import com.manage.recipe.model.dao.Recipe;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author AnantDibakar
 * @date 26/07/2023
 * @brief This class holds the mapping logic from DTO to DAO objects.
 */
@Component
public class RecipeModelMapper {

    public RecipeDTO mapToRecipeDTO(Recipe recipe) {
        return RecipeDTO.builder()
                .recipeId(recipe.getId())
                .name(recipe.getName())
                .instructions(recipe.getInstructions())
                .ingredients(recipe.getIngredients())
                .foodCategory(recipe.getFoodCategory())
                .servings(recipe.getServings())
                .build();
    }

    public Recipe mapToRecipe(RecipeDTO recipeDTO) {
        return Recipe.builder()
                .name(recipeDTO.getName())
                .instructions(recipeDTO.getInstructions())
                .ingredients(recipeDTO.getIngredients())
                .foodCategory(recipeDTO.getFoodCategory())
                .servings(recipeDTO.getServings())
                .build();
    }

    public RecipeResponseDTO mapToRecipeDTOlist(List<Recipe> recipeList) {
        return RecipeResponseDTO.builder()
                .recipeDTOList(recipeList.stream().map(this::mapToRecipeDTO).collect(Collectors.toList()))
                .status(HttpStatus.OK.value())
                .build();
    }

    public Recipe updatedRecipe(Recipe recipe, RecipeDTO recipeDTO) {
        recipe.setName(recipeDTO.getName());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setServings(recipeDTO.getServings());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFoodCategory(recipeDTO.getFoodCategory());
        return recipe;
    }
}
