package com.manage.recipe.unit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.exception.RecipeNotFoundException;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.Ingredient;
import com.manage.recipe.model.dao.Recipe;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.repository.RecipeRepository;
import com.manage.recipe.service.RecipeService;
import com.manage.recipe.util.RecipeModelMapper;
import com.manage.recipe.util.RecipeSearchSpecifications;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    private RecipeDTO recipeDTO;
    private Recipe recipe;

    @Mock
    RecipeModelMapper recipeModelMapper;

    @Mock
    private RecipeSearchSpecifications recipeSearchSpecifications;

    @BeforeEach
    public void setUp() {
        recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("ingredient1").build(),
                        Ingredient.builder().ingredientName("ingredient2").build()))
                .name("testRecipe")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
        recipe = Recipe.builder().build();
    }

    @AfterEach
    public void tearDown() {
        recipeDTO = null;
        recipe = null;
    }

    @Test
    public void test_add_recipe_success() {

        when(recipeModelMapper.mapToRecipe(any(RecipeDTO.class))).thenReturn(recipe);
        when(recipeModelMapper.mapToRecipeDTO(any(Recipe.class))).thenReturn(recipeDTO);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        RecipeDTO savedRecipeDTO = recipeService.addNewRecipe(recipeDTO);
        Assertions.assertNotNull(savedRecipeDTO);
        Assertions.assertEquals(recipeDTO.getName(), savedRecipeDTO.getName());
        verify(recipeRepository, times(1)).save(any());
    }

    @Test
    public void test_add_invalid_recipe_name() {
        recipeDTO.setName("");
        Exception exception =
                assertThrows(InvalidRecipeRequestException.class, () -> recipeService.addNewRecipe(recipeDTO));
        Assertions.assertTrue(exception.getMessage().contains("Recipe name can not be null or empty"));
    }

    @Test
    public void test_add_already_existing_recipe() {
        when(recipeRepository.findByName(anyString()))
                .thenReturn(Optional.of(Recipe.builder().build()));
        Exception exception =
                assertThrows(InvalidRecipeRequestException.class, () -> recipeService.addNewRecipe(recipeDTO));
        Assertions.assertTrue(exception.getMessage().contains("Recipe name already exists"));
    }

    @Test
    public void test_fetch_all_recipies_success() {
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        recipeDTOList.add(recipeDTO);
        List<Recipe> recipeList = new ArrayList<>();
        recipeList.add(Recipe.builder().build());
        when(recipeModelMapper.mapToRecipeDTOlist(anyList()))
                .thenReturn(
                        RecipeResponseDTO.builder().recipeDTOList(recipeDTOList).build());
        when(recipeRepository.findAll()).thenReturn(recipeList);
        RecipeResponseDTO recipeDTOListResponse = recipeService.fetchAllRecipes();
        Assertions.assertNotNull(recipeDTOListResponse);
        Assertions.assertEquals(
                recipeList.size(), recipeDTOListResponse.getRecipeDTOList().size());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void test_fetch_recipe_by_id() {

        Long recipeId = 1L;
        Recipe mockedRecipe = Recipe.builder().id(recipeId).name("Test Recipe").build();
        RecipeDTO mockedRecipeDTO =
                RecipeDTO.builder().recipeId(1L).name("Test Recipe").build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockedRecipe));
        when(recipeModelMapper.mapToRecipeDTO(any())).thenReturn(mockedRecipeDTO);
        RecipeDTO result = recipeService.fetchRecipeById(recipeId);
        Assertions.assertEquals(recipeId, result.getRecipeId());
        Assertions.assertEquals("Test Recipe", result.getName());
    }

    @Test
    public void test_fetch_recipe_by_invalid_id() {

        Long recipeId = UUID.randomUUID().getMostSignificantBits();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        assertThrows(RecipeNotFoundException.class, () -> recipeService.fetchRecipeById(recipeId));
    }

    @Test
    public void test_update_recipe_by_recipe_id() {

        Long recipeId = 1L;
        RecipeDTO recipeDTO = RecipeDTO.builder().name("Updated Recipe").build();

        Recipe existingRecipe =
                Recipe.builder().id(recipeId).name("Original Recipe").build();

        Recipe updatedRecipe = Recipe.builder().name("Updated Recipe").build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeModelMapper.updatedRecipe(any(Recipe.class), any(RecipeDTO.class)))
                .thenReturn(updatedRecipe);
        when(recipeRepository.save(any())).thenReturn(updatedRecipe);
        when(recipeModelMapper.mapToRecipeDTO(any(Recipe.class))).thenReturn(recipeDTO);

        RecipeDTO result = recipeService.updateRecipe(recipeId, recipeDTO);

        Assertions.assertEquals(recipeDTO.getName(), result.getName());
    }

    @Test
    public void test_update_recipe_by_invalid_id() {

        Long recipeId = UUID.randomUUID().getMostSignificantBits();
        RecipeDTO recipeDTOtoUpdate = RecipeDTO.builder().build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        assertThrows(RecipeNotFoundException.class, () -> recipeService.updateRecipe(recipeId, recipeDTOtoUpdate));
    }

    @Test
    public void test_remove_recipe_by_id() {

        Long recipeId = 1L;
        Recipe existingRecipe =
                Recipe.builder().id(recipeId).name("Test Recipe").build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));

        recipeService.removeRecipe(recipeId);

        verify(recipeRepository).findById(recipeId);

        verify(recipeRepository).delete(existingRecipe);
    }

    @Test
    public void test_remove_recipe_by_invalid_id() {

        Long recipeId = UUID.randomUUID().getMostSignificantBits();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        assertThrows(RecipeNotFoundException.class, () -> recipeService.removeRecipe(recipeId));
    }

    @Test
    public void test_search_recipes() {

        RecipeFilterSearchDTO filterDTO = RecipeFilterSearchDTO.builder()
                .name("Test Recipe")
                .foodCategory("VEG")
                .servings(1)
                .includedIngredients(List.of("salt"))
                .excludedIngredients(List.of("coriander"))
                .searchTextInInstructions("instruction")
                .build();

        Pageable pageable = Pageable.unpaged();

        List<Recipe> recipeList = new ArrayList<>();
        Recipe recipe = Recipe.builder()
                .id(1L)
                .name("Test Recipe")
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("salt").build(),
                        Ingredient.builder().ingredientName("pepper").build()))
                .instructions("instruction")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();

        recipeList.add(recipe);
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .recipeId(1L)
                .name("Test Recipe")
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("salt").build(),
                        Ingredient.builder().ingredientName("pepper").build()))
                .instructions("test instruction")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();
        recipeDTOList.add(recipeDTO);
        RecipeResponseDTO recipeResponseDTO =
                RecipeResponseDTO.builder().recipeDTOList(recipeDTOList).build();

        when(recipeSearchSpecifications.getRecipeSearchSpecification(any(RecipeFilterSearchDTO.class)))
                .thenReturn(Specification.where(null));
        when(recipeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(new PageImpl<>(recipeList));

        when(recipeModelMapper.mapToRecipeDTOlist(recipeList)).thenReturn(recipeResponseDTO);

        RecipeResponseDTO result = recipeService.searchRecipes(filterDTO, pageable);

        verify(recipeSearchSpecifications).getRecipeSearchSpecification(filterDTO);
        verify(recipeRepository).findAll(any(Specification.class), eq(pageable));
        verify(recipeModelMapper).mapToRecipeDTOlist(recipeList);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getRecipeDTOList().stream()
                .anyMatch(recipeDto -> recipeDto.getName().equalsIgnoreCase("Test Recipe")
                        && recipeDto.getServings() == 1
                        && recipeDto.getInstructions().equalsIgnoreCase("test instruction")));
    }
}
