package com.manage.recipe.service;

import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.exception.RecipeNotFoundException;
import com.manage.recipe.model.dao.RecipeDAO;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.repository.RecipeRepository;
import com.manage.recipe.util.RecipeModelMapper;
import com.manage.recipe.util.RecipeSearchSpecifications;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

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
        logger.debug("Saving recipe object to database");
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
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method fetches all recipe objects .
     *        Returned list is then wrapped to RecipeResponseDTO object.
     * @throws RecipeNotFoundException when no recipes found
     * @return RecipeResponseDTO object
     */
    public RecipeResponseDTO fetchAllRecipes() {
        logger.info("Fetch request for all recipes");
        List<RecipeDAO> recipeDAOList = recipeRepository.findAll();
        if (recipeDAOList.size() == 0) {
            logger.error("There are no recipes");
            throw new RecipeNotFoundException("There are no recipes");
        }
        return recipeModelMapper.mapToRecipeDTOlist(recipeDAOList);
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method fetches recipe by recipe id.
     *        Returned RecipeDAO is then converted to  RecipeDTO for presentation layer.
     * @throws RecipeNotFoundException when no recipe found by recipe id.
     * @return RecipeDTO object
     */
    public RecipeDTO fetchRecipeById(Long recipeId) {
        logger.debug("Fetching recipe by id {}", recipeId);
        RecipeDAO recipeDAO = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found by given Id"));
        return recipeModelMapper.mapToRecipeDTO(recipeDAO);
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method updates an existing recipe by recipe id.
     *        Updated RecipeDAO is then converted to  RecipeDTO for presentation layer.
     * @throws RecipeNotFoundException when no recipe found by recipe id.
     * @return RecipeDTO object
     */
    public RecipeDTO updateRecipe(Long recipeId, RecipeDTO recipeDTO) {
        logger.debug("Updating recipe with id {}", recipeId);
        RecipeDAO recipeDAO = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found by given Id"));
        return recipeModelMapper.mapToRecipeDTO(
                recipeRepository.save(recipeModelMapper.updatedRecipe(recipeDAO, recipeDTO)));
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method removes an existing recipe by recipe id.
     * @param recipeId
     * @throws RecipeNotFoundException when no recipe found by recipe id.
     */
    public void removeRecipe(Long recipeId) {
        logger.warn("Removing recipe with id {}", recipeId);
        RecipeDAO recipeDAO = recipeRepository
                .findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found by given Id"));
        recipeRepository.delete(recipeDAO);
    }
    /**
     * @author AnantDibakar
     * @date 26/07/2023
     * @brief This method searches recipe by specifications and pageable object.
     *        Returned RecipeDAOlist is then converted to  RecipeDTO for presentation layer.
     * @return RecipeResponseDTO object
     */
    public RecipeResponseDTO searchRecipes(RecipeFilterSearchDTO filterDTO, Pageable pageable) {
        logger.info("Search request initiated for recipe");
        Specification<RecipeDAO> searchSpecification =
                recipeSearchSpecifications.getRecipeSearchSpecification(filterDTO);
        Page<RecipeDAO> page = recipeRepository.findAll(searchSpecification, pageable);
        return recipeModelMapper.mapToRecipeDTOlist(page.getContent());
    }
}
