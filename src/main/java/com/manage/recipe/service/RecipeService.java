package com.manage.recipe.service;

import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.exception.RecipeNotFoundException;
import com.manage.recipe.model.dao.Recipe;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.repository.RecipeRepository;
import com.manage.recipe.util.RecipeModelMapper;
import com.manage.recipe.util.RecipeSearchSpecifications;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * This service class holds the business logic of recipe manager service.
 * @author AnantDibakar
 * @date 26/07/2023
 */
@Service
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeModelMapper recipeModelMapper;
    private final RecipeSearchSpecifications recipeSearchSpecifications;

    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeModelMapper recipeModelMapper,
            RecipeSearchSpecifications recipeSearchSpecifications) {
        this.recipeRepository = recipeRepository;
        this.recipeModelMapper = recipeModelMapper;
        this.recipeSearchSpecifications = recipeSearchSpecifications;
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method create a Recipe object . DTO object is converted to DAO before saving to database
     *         and DAO object is converted to DTO for presentation layer.
     * @param  recipeDTO
     * @throws InvalidRecipeRequestException when recipe name is null/empty or existing name.
     * @return RecipeDTO object
     */
    public RecipeDTO addNewRecipe(RecipeDTO recipeDTO) {
        log.debug("Saving recipe object to database {}", recipeDTO);
        if (recipeDTO.getName() == null || recipeDTO.getName().isEmpty()) {
            log.error("Recipe name can not be null or empty");
            throw new InvalidRecipeRequestException("Recipe name can not be null or empty");
        }
        if (recipeRepository.findByName(recipeDTO.getName()).isPresent()) {
            log.error("Recipe name already exists");
            throw new InvalidRecipeRequestException("Recipe name already exists");
        }
        return recipeModelMapper.mapToRecipeDTO(recipeRepository.save(recipeModelMapper.mapToRecipe(recipeDTO)));
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method fetches all recipe objects .
     *     Returned list is then wrapped to RecipeResponseDTO object.
     * @return RecipeResponseDTO object
     */
    public RecipeResponseDTO fetchAllRecipes() {
        log.info("Fetch request for all recipes");
        List<Recipe> recipeList = recipeRepository.findAll();
        return recipeModelMapper.mapToRecipeDTOlist(recipeList);
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method fetches recipe by recipe id.
     *        Returned Recipe is then converted to  RecipeDTO for presentation layer.
     * @throws RecipeNotFoundException when no recipe found by recipe id.
     * @return RecipeDTO object
     */
    public RecipeDTO fetchRecipeById(Long recipeId) {
        log.debug("Fetching recipe by id {}", recipeId);
        Recipe recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found by given Id"));
        return recipeModelMapper.mapToRecipeDTO(recipe);
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method updates an existing recipe by recipe id.
     *        Updated Recipe is then converted to  RecipeDTO for presentation layer.
     * @throws RecipeNotFoundException when no recipe found by recipe id.
     * @return RecipeDTO object
     */
    public RecipeDTO updateRecipe(Long recipeId, RecipeDTO recipeDTO) {
        log.debug("Updating recipe with id {}", recipeId);
        Recipe recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found by given Id"));
        return recipeModelMapper.mapToRecipeDTO(
                recipeRepository.save(recipeModelMapper.updatedRecipe(recipe, recipeDTO)));
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method removes an existing recipe by recipe id.
     * @param recipeId
     * @throws RecipeNotFoundException when no recipe found by recipe id.
     */
    public void removeRecipe(Long recipeId) {
        log.warn("Removing recipe with id {}", recipeId);
        Recipe recipe = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found by given Id"));
        recipeRepository.delete(recipe);
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method searches recipe by specifications and pageable object.
     *        Returned Recipelist is then converted to  RecipeDTO for presentation layer.
     * @return RecipeResponseDTO object
     */
    public RecipeResponseDTO searchRecipes(RecipeFilterSearchDTO filterDTO, Pageable pageable) {
        log.info("Search request initiated for recipe");
        Specification<Recipe> searchSpecification = recipeSearchSpecifications.getRecipeSearchSpecification(filterDTO);
        Page<Recipe> page = recipeRepository.findAll(searchSpecification, pageable);
        return recipeModelMapper.mapToRecipeDTOlist(page.getContent());
    }
}
