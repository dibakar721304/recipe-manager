package com.manage.recipe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.recipe.controller.RecipeController;
import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.exception.RecipeNotFoundException;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.service.RecipeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    private RecipeDTO recipeDTO;
    private List<RecipeDTO> recipeDTOList;

    private static final String RECIPE_END_POINT = "/recipes";

    @BeforeEach
    public void setup() {

        recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("ingredient1").build(),
                        IngredientDAO.builder().ingredientName("ingredient2").build()))
                .name("testRecipe")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
    }

    @AfterEach
    public void tearDown() {
        recipeDTO = null;
        recipeDTOList = null;
    }

    @Test
    public void test_create_recipe_success() throws Exception {

        when(recipeService.addNewRecipe(any(RecipeDTO.class))).thenReturn(recipeDTO);
        mockMvc.perform(getRequestBuilder())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(recipeDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients.size()")
                        .value(recipeDTO.getIngredients().size()));
        verify(recipeService).addNewRecipe(any(RecipeDTO.class));
    }

    @Test
    public void test_create_recipe_invalid_recipe_name() throws Exception {
        when(recipeService.addNewRecipe(any(RecipeDTO.class)))
                .thenThrow(new InvalidRecipeRequestException("Recipe name can not be null or empty"));
        mockMvc.perform(getRequestBuilder())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Recipe name can not be null or empty"));
        verify(recipeService).addNewRecipe(any(RecipeDTO.class));
    }

    @Test
    public void test_create_recipe_already_existing_recipe_name() throws Exception {

        when(recipeService.addNewRecipe(any(RecipeDTO.class)))
                .thenThrow(new InvalidRecipeRequestException("Recipe name already exists"));
        mockMvc.perform(getRequestBuilder())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Recipe name already exists"));
        verify(recipeService).addNewRecipe(any(RecipeDTO.class));
    }

    private MockHttpServletRequestBuilder getRequestBuilder() throws JsonProcessingException {
        return MockMvcRequestBuilders.post(RECIPE_END_POINT)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(recipeDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void test_fetch_all_recipes_success() throws Exception {
        recipeDTOList = new ArrayList<>();
        recipeDTOList.add(recipeDTO);
        RecipeResponseDTO recipeResponseDTO =
                RecipeResponseDTO.builder().recipeDTOList(recipeDTOList).build();
        when(recipeService.fetchAllRecipes()).thenReturn(recipeResponseDTO);
        mockMvc.perform(MockMvcRequestBuilders.get(RECIPE_END_POINT)).andExpect(status().isOk());
        verify(recipeService).fetchAllRecipes();
    }

    @Test
    public void test_fetch_all_recipes_bad_request() throws Exception {
        when(recipeService.fetchAllRecipes()).thenThrow(new RecipeNotFoundException("There are no recipes"));
        mockMvc.perform(MockMvcRequestBuilders.get(RECIPE_END_POINT)).andExpect(status().isNotFound());
        verify(recipeService).fetchAllRecipes();
    }
}
