package com.manage.recipe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.recipe.controller.RecipeController;
import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.service.RecipeService;
import java.util.Arrays;
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
import org.springframework.test.web.servlet.ResultActions;
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

    RecipeDTO recipeDTO;
    ResultActions resultActions;

    private static final String CREATE_RECIPE_END_POINT = "/recipes/add";

    @BeforeEach
    public void setup() {

        recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList("salt", "pepper"))
                .name("testRecipe")
                .isVegetarian(true)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
    }

    @AfterEach
    public void tearDown() {
        recipeDTO = null;
    }

    @Test
    public void test_create_recipe_success() throws Exception {

        when(recipeService.addNewRecipe(any(RecipeDTO.class))).thenReturn(recipeDTO);
        mockMvc.perform(getRequestBuilder())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(recipeDTO.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients.size()")
                        .value(recipeDTO.getIngredients().size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[0]")
                        .value(recipeDTO.getIngredients().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ingredients[1]")
                        .value(recipeDTO.getIngredients().get(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.vegetarian").value(recipeDTO.isVegetarian()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.servings").value(recipeDTO.getServings()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instructions").value(recipeDTO.getInstructions()))
                .andDo(print());
        verify(recipeService).addNewRecipe(any(RecipeDTO.class));
    }

    @Test
    public void test_create_recipe_invalid_recipe_name() throws Exception {
        when(recipeService.addNewRecipe(any(RecipeDTO.class)))
                .thenThrow(new InvalidRecipeRequestException("Recipe name can not be null or empty"));
        mockMvc.perform(getRequestBuilder())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Recipe name can not be null or empty"));
        verify(recipeService).addNewRecipe(any(RecipeDTO.class));
    }

    @Test
    public void test_create_recipe_already_existing_recipe_name() throws Exception {

        when(recipeService.addNewRecipe(any(RecipeDTO.class)))
                .thenThrow(new InvalidRecipeRequestException("Recipe name already exists"));
        mockMvc.perform(getRequestBuilder())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Recipe name already exists"));
        verify(recipeService).addNewRecipe(any(RecipeDTO.class));
    }

    private MockHttpServletRequestBuilder getRequestBuilder() throws JsonProcessingException {
        return MockMvcRequestBuilders.post(CREATE_RECIPE_END_POINT)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(recipeDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }
}
