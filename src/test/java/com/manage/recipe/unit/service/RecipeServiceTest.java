package com.manage.recipe.unit.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.exception.RecipeNotFoundException;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import com.manage.recipe.model.dao.RecipeDAO;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.repository.RecipeRepository;
import com.manage.recipe.service.RecipeService;
import com.manage.recipe.util.RecipeModelMapper;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    private RecipeDTO recipeDTO;
    private RecipeDAO recipeDAO;
    private List<RecipeDTO> recipeDTOList;

    @Mock
    RecipeModelMapper recipeModelMapper;

    @BeforeEach
    public void setUp() {
        recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("ingredient1").build(),
                        IngredientDAO.builder().ingredientName("ingredient2").build()))
                .name("testRecipe")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
        recipeDAO = RecipeDAO.builder().build();
    }

    @AfterEach
    public void tearDown() {
        recipeDTO = null;
        recipeDAO = null;
    }

    @Test
    public void test_add_recipe_success() {

        when(recipeModelMapper.mapToRecipeDAO(any(RecipeDTO.class))).thenReturn(recipeDAO);
        when(recipeModelMapper.mapToRecipeDTO(any(RecipeDAO.class))).thenReturn(recipeDTO);
        when(recipeRepository.save(any(RecipeDAO.class))).thenReturn(recipeDAO);
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
                .thenReturn(Optional.of(RecipeDAO.builder().build()));
        Exception exception =
                assertThrows(InvalidRecipeRequestException.class, () -> recipeService.addNewRecipe(recipeDTO));
        Assertions.assertTrue(exception.getMessage().contains("Recipe name already exists"));
    }

    @Test
    public void test_fetch_all_recipies_success() {
        recipeDTOList = new ArrayList<>();
        recipeDTOList.add(recipeDTO);
        List<RecipeDAO> recipeDAOList = new ArrayList<>();
        recipeDAOList.add(RecipeDAO.builder().build());
        when(recipeModelMapper.mapToRecipeDTOlist(anyList()))
                .thenReturn(
                        RecipeResponseDTO.builder().recipeDTOList(recipeDTOList).build());
        when(recipeRepository.findAll()).thenReturn(recipeDAOList);
        RecipeResponseDTO recipeDTOListResponse = recipeService.fetchAllRecipes();
        Assertions.assertNotNull(recipeDTOListResponse);
        Assertions.assertEquals(
                recipeDAOList.size(), recipeDTOListResponse.getRecipeDTOList().size());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void test_fetch_all_recipies_not_found() {
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(RecipeNotFoundException.class, () -> recipeService.fetchAllRecipes());
        Assertions.assertTrue(exception.getMessage().contains("There are no recipes"));
    }
}
