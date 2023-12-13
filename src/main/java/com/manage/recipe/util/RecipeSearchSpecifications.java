package com.manage.recipe.util;

import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.Ingredient;
import com.manage.recipe.model.dao.Recipe;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * This class holds the searching specifications and search criteria logic.
 * @author AnantDibakar
 * @date 26/07/2023
 */
@Component
public class RecipeSearchSpecifications {
    private static final Logger logger = LoggerFactory.getLogger(RecipeSearchSpecifications.class);
    private static final String NAME = "name";

    private static final String FOOD_CATEGORY = "foodCategory";
    private static final String SERVINGS_NUMBER = "servings";
    private static final String INGREDIENTS = "ingredients";
    private static final String INGREDIENT_NAME = "ingredientName";
    private static final String INSTRUCTIONS = "instructions";
    private static final String RECIPE_ID = "id";
    /**
     * This method checks if a specified food category exists or not
     * @author AnantDibakar
     * @date 30/07/2023
     * @param    name, example: VEG,UNKOWN
     * @return A specification object if found or else returns null
     */
    public Specification<Recipe> hasRecipeName(String name) {
        return (reciperoot, query, criteriaBuilder) -> {
            if (null == name) {
                return null;
            }
            return criteriaBuilder.equal(reciperoot.get(NAME), name);
        };
    }
    /**
     * This method checks if a specified food category exists or not
     * @author AnantDibakar
     * @date 26/07/2023
     * @param    foodCategory, example: VEG,UNKOWN
     * @return A specification object if found or else returns null
     */
    public Specification<Recipe> hasFoodCategory(FoodCategory foodCategory) {
        return (reciperoot, query, criteriaBuilder) -> {
            if (null == foodCategory) {
                return null;
            }
            return criteriaBuilder.equal(reciperoot.get(FOOD_CATEGORY), foodCategory);
        };
    }
    /**
     * This method checks if a specified number of servings exists or not
     * @author AnantDibakar
     * @date 26/07/2023
     * @param    servings , example 1,2,3
     * @return A specification object if found or else returns null
     */
    public Specification<Recipe> hasServings(Integer servings) {
        return (reciperoot, query, criteriaBuilder) -> {
            if (null == servings) {
                return null;
            }
            return criteriaBuilder.equal(reciperoot.get(SERVINGS_NUMBER), servings);
        };
    }
    /**
     * This method checks if a specified including ingredients exists or not
     * @author AnantDibakar
     * @date 26/07/2023
     * @param    includeIngredients of type List<String>, example ["salt","sugar"]
     * @return A specification object if found or else returns null
     */
    public Specification<Recipe> hasIncludeIngredients(List<String> includeIngredients) {
        return (reciperoot, query, criteriaBuilder) -> {
            if (null == includeIngredients || includeIngredients.isEmpty()) {
                return null;
            }
            return reciperoot.joinList(INGREDIENTS).get(INGREDIENT_NAME).in(includeIngredients);
        };
    }
    /**
     * This method checks recipe table and ingedient table by performing an inner join and checks if the ingredient
     * does not exist for specific recipe
     * @author AnantDibakar
     * @date 26/07/2023
     * @param    excludeIngredients of type List<String>, example ["salt","sugar"]
     * @return A specification object if found or else returns null
     */
    public Specification<Recipe> hasExcludeIngredients(List<String> excludeIngredients) {
        return (reciperoot, query, criteriaBuilder) -> {
            if (null == excludeIngredients || excludeIngredients.isEmpty()) {
                return null;
            }
            Subquery<Long> subqueryExcludedIngredients = query.subquery(Long.class);
            Root<Recipe> subqueryRecipe = subqueryExcludedIngredients.from(Recipe.class);
            Join<Recipe, Ingredient> subqueryJoinIngredients = subqueryRecipe.joinList(INGREDIENTS, JoinType.INNER);
            subqueryExcludedIngredients
                    .select(subqueryRecipe.get(RECIPE_ID))
                    .where(subqueryJoinIngredients.get(INGREDIENT_NAME).in(excludeIngredients));

            return criteriaBuilder.not(reciperoot.get(RECIPE_ID).in(subqueryExcludedIngredients));
        };
    }
    /**
     * This method checks if a specified word is present in the instructions of recipe
     * @author AnantDibakar
     * @date 26/07/2023
     * @param    searchText of type String , example: "ovan","stir"
     * @return A specification object if found or else returns null
     */
    public Specification<Recipe> hasSearchText(String searchText) {
        return (reciperoot, query, criteriaBuilder) -> {
            if (null == searchText || searchText.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(reciperoot.get(INSTRUCTIONS)), "%" + searchText.toLowerCase() + "%");
        };
    }
    /**
     * This method performs a query with all the search criteria represented by a RecipeFilterSearchDTO.
     * There is no specific group of combination search, it checks for every condition and combine and build the query accordingly
     * @author AnantDibakar
     * @date 26/07/2023
     * @param    recipeFilterSearchDTO of type RecipeFilterSearchDTO . Here one or more search fields can be present.
     * @return A specification object
     */
    public Specification<Recipe> getRecipeSearchSpecification(RecipeFilterSearchDTO recipeFilterSearchDTO) {
        logger.debug("Search has been initiated with request {}", recipeFilterSearchDTO);
        return Specification.where(hasRecipeName(recipeFilterSearchDTO.getName()))
                .and(hasFoodCategory(recipeFilterSearchDTO.getFoodCategoryEnum()))
                .and(hasServings(recipeFilterSearchDTO.getServings()))
                .and(hasIncludeIngredients(recipeFilterSearchDTO.getIncludedIngredients()))
                .and(hasExcludeIngredients(recipeFilterSearchDTO.getExcludedIngredients()))
                .and(hasSearchText(recipeFilterSearchDTO.getSearchTextInInstructions()));
    }
}
