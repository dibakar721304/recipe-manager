package com.manage.recipe.util;

import com.manage.recipe.model.dao.RecipeDAO;
import com.manage.recipe.model.dto.RecipeDTO;
import org.springframework.stereotype.Component;

@Component
public class RecipeModelMapper {

    public RecipeDTO mapToRecipeDTO(RecipeDAO recipeDAO) {
        return RecipeDTO.builder()
                .name(recipeDAO.getName())
                .instructions(recipeDAO.getInstructions())
                .ingredients(recipeDAO.getIngredients())
                .isVegetarian(recipeDAO.isVegetarian())
                .servings(recipeDAO.getServings())
                .build();
    }

    public RecipeDAO mapToRecipeDAO(RecipeDTO recipeDTO) {
        return RecipeDAO.builder()
                .name(recipeDTO.getName())
                .instructions(recipeDTO.getInstructions())
                .ingredients(recipeDTO.getIngredients())
                .isVegetarian(recipeDTO.isVegetarian())
                .servings(recipeDTO.getServings())
                .build();
    }
}
