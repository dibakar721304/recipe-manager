package com.manage.recipe.util;

import com.manage.recipe.model.dao.RecipeDAO;
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

    public RecipeDTO mapToRecipeDTO(RecipeDAO recipeDAO) {
        return RecipeDTO.builder()
                .recipeId(recipeDAO.getId())
                .name(recipeDAO.getName())
                .instructions(recipeDAO.getInstructions())
                .ingredients(recipeDAO.getIngredients())
                .foodCategory(recipeDAO.getFoodCategory())
                .servings(recipeDAO.getServings())
                .build();
    }

    public RecipeDAO mapToRecipeDAO(RecipeDTO recipeDTO) {
        return RecipeDAO.builder()
                .name(recipeDTO.getName())
                .instructions(recipeDTO.getInstructions())
                .ingredients(recipeDTO.getIngredients())
                .foodCategory(recipeDTO.getFoodCategory())
                .servings(recipeDTO.getServings())
                .build();
    }

    public RecipeResponseDTO mapToRecipeDTOlist(List<RecipeDAO> recipeDAOList) {
        return RecipeResponseDTO.builder()
                .recipeDTOList(recipeDAOList.stream().map(this::mapToRecipeDTO).collect(Collectors.toList()))
                .status(HttpStatus.OK)
                .build();
    }

    public RecipeDAO updatedRecipe(RecipeDAO recipeDAO, RecipeDTO recipeDTO) {
        recipeDAO.setName(recipeDTO.getName());
        recipeDAO.setIngredients(recipeDTO.getIngredients());
        recipeDAO.setServings(recipeDTO.getServings());
        recipeDAO.setInstructions(recipeDTO.getInstructions());
        // recipeDAO.setVegetarian(recipeDTO.isVegetarian());
        recipeDAO.setFoodCategory(recipeDTO.getFoodCategory());
        return recipeDAO;
    }
}
