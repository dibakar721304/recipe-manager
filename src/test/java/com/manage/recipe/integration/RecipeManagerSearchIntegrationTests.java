package com.manage.recipe.integration;

import com.manage.recipe.RecipeManagerApplication;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.Ingredient;
import com.manage.recipe.model.dao.Recipe;
import com.manage.recipe.model.dto.AuthRequestDTO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = RecipeManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RecipeManagerSearchIntegrationTests {
    @LocalServerPort
    private int port;

    @Value("${recipe.host.url:http://localhost}")
    private String baseUrl;

    @Value("${recipe.context.path:/recipes}")
    private String context;

    private static TestRestTemplate testRestTemplate;
    private String contextUrl;
    private String token;

    @Autowired
    private RecipeManagerTestRepository recipeManagerTestRepository;

    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeFilterSearchDTO recipeFilterSearchDTO;
    private static final String SEARCH = "/search";
    private static final String QUESTION_MARK = "?";
    private static final String QUERY_APPEND_CHARACTER = "&";
    private static final String EQUAL_CHARACTER = "=";
    private static final String RECIPE_NAME = "name";

    private static final String FOOD_CATEGORY_SEARCH = "foodCategory";
    private static final String INCLUDE_INGREDIENT_SEARCH = "includedIngredients";
    private static final String EXCLUDE_INGREDIENT_SEARCH = "excludedIngredients";
    private static final String SERVINGS_SEARCH = "servings";
    private static final String INSTRUCTION_TEXT_SEARCH = "searchTextInInstructions";

    @BeforeAll
    public static void init() {
        testRestTemplate = new TestRestTemplate();
    }

    @BeforeEach
    public void setUp() {
        token = getAuthenticationToken();
        recipeFilterSearchDTO = RecipeFilterSearchDTO.builder().build();
        contextUrl = baseUrl.concat(":").concat(port + "").concat(context);
        Recipe firstRrecipe = Recipe.builder()
                .name("recipe1")
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("ingredient1").build(),
                        Ingredient.builder().ingredientName("ingredient2").build()))
                .instructions("instruction1")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();
        Recipe secondRrecipe = Recipe.builder()
                .name("recipe2")
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("ingredient3").build(),
                        Ingredient.builder().ingredientName("ingredient4").build()))
                .instructions("instruction2")
                .foodCategory(FoodCategory.UNKOWN)
                .servings(2)
                .build();
        recipeList.add(firstRrecipe);
        recipeList.add(secondRrecipe);
        recipeManagerTestRepository.saveAll(recipeList);
    }

    public String getAuthenticationToken() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("admin", "admin");

        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                baseUrl.concat(":").concat(port + "") + "/auth//login", authRequestDTO, Map.class);
        Map<String, String> responseBody = response.getBody();
        return Objects.requireNonNull(responseBody).getOrDefault("token", "");
    }

    @AfterEach
    public void tearDown() {
        recipeManagerTestRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllVegetarianFoodList() {
        String foodCategory = "VEG";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeFilterSearchDTO> requestEntity = new HttpEntity<>(recipeFilterSearchDTO, headers);
        ResponseEntity<RecipeResponseDTO> response = testRestTemplate.exchange(
                contextUrl + SEARCH + QUESTION_MARK + FOOD_CATEGORY_SEARCH + EQUAL_CHARACTER + foodCategory,
                HttpMethod.GET,
                requestEntity,
                RecipeResponseDTO.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(
                Objects.requireNonNull(response.getBody()).getRecipeDTOList().size() > 0);
    }

    @Test
    public void shouldSearchForServingsAndIncludedIngredients() {
        int numberOfServings = 1;
        String includeIngredient = "ingredient1";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeFilterSearchDTO> requestEntity = new HttpEntity<>(recipeFilterSearchDTO, headers);
        ResponseEntity<RecipeResponseDTO> response = testRestTemplate.exchange(
                contextUrl
                        + SEARCH
                        + QUESTION_MARK
                        + SERVINGS_SEARCH
                        + EQUAL_CHARACTER
                        + numberOfServings
                        + QUERY_APPEND_CHARACTER
                        + INCLUDE_INGREDIENT_SEARCH
                        + includeIngredient,
                HttpMethod.GET,
                requestEntity,
                RecipeResponseDTO.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(
                Objects.requireNonNull(response.getBody()).getRecipeDTOList().size() > 0);
    }

    @Test
    public void shouldSearchForExcludedIngredientsWithInstructionSearch() {
        String excludeIngredientSearch = "ingredient5";
        String searchTextInInstruction = "instruction1";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeFilterSearchDTO> requestEntity = new HttpEntity<>(recipeFilterSearchDTO, headers);
        ResponseEntity<RecipeResponseDTO> response = testRestTemplate.exchange(
                contextUrl
                        + SEARCH
                        + QUESTION_MARK
                        + EXCLUDE_INGREDIENT_SEARCH
                        + excludeIngredientSearch
                        + QUERY_APPEND_CHARACTER
                        + INSTRUCTION_TEXT_SEARCH
                        + EQUAL_CHARACTER
                        + searchTextInInstruction,
                HttpMethod.GET,
                requestEntity,
                RecipeResponseDTO.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(
                Objects.requireNonNull(response.getBody()).getRecipeDTOList().size() > 0);
    }

    @Test
    public void shouldSearchForAllFilters() {
        String name = "recipe1";
        String foodCategory = "VEG";
        int numberOfServings = 1;
        String includeIngredient = "ingredient1";
        String excludeIngredientSearch = "ingredient5";
        String searchTextInInstruction = "instruction1";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeFilterSearchDTO> requestEntity = new HttpEntity<>(recipeFilterSearchDTO, headers);
        ResponseEntity<RecipeResponseDTO> response = testRestTemplate.exchange(
                contextUrl
                        + SEARCH
                        + QUESTION_MARK
                        + RECIPE_NAME
                        + name
                        + QUERY_APPEND_CHARACTER
                        + FOOD_CATEGORY_SEARCH
                        + foodCategory
                        + QUERY_APPEND_CHARACTER
                        + SERVINGS_SEARCH
                        + numberOfServings
                        + QUERY_APPEND_CHARACTER
                        + INCLUDE_INGREDIENT_SEARCH
                        + includeIngredient
                        + QUERY_APPEND_CHARACTER
                        + EXCLUDE_INGREDIENT_SEARCH
                        + excludeIngredientSearch
                        + QUERY_APPEND_CHARACTER
                        + INSTRUCTION_TEXT_SEARCH
                        + EQUAL_CHARACTER
                        + searchTextInInstruction,
                HttpMethod.GET,
                requestEntity,
                RecipeResponseDTO.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(
                Objects.requireNonNull(response.getBody()).getRecipeDTOList().size() > 0);
    }
}
