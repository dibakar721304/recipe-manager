package com.manage.recipe.service;

import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.repository.RecipeRepository;
import com.manage.recipe.util.RecipeModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final RecipeModelMapper recipeModelMapper;

    public RecipeService(RecipeRepository recipeRepository, RecipeModelMapper recipeModelMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeModelMapper = recipeModelMapper;
    }

    public RecipeDTO addNewRecipe(RecipeDTO recipeDTO) {
        logger.info("Saving recipe object to database");
        if (recipeDTO.getName() == null || recipeDTO.getName().isEmpty()) {
            logger.error("Recipe name can not be null or empty");
            throw new InvalidRecipeRequestException("Recipe name can not be null or empty");
        }
        if (recipeRepository.findByName(recipeDTO.getName()).isPresent()) {
            logger.error("Recipe name already exists");
            throw new InvalidRecipeRequestException("Recipe name already exists");
        }
        return recipeModelMapper.mapToRecipeDTO(recipeRepository.save(recipeModelMapper.mapToRecipeDAO(recipeDTO)));
    }
}
