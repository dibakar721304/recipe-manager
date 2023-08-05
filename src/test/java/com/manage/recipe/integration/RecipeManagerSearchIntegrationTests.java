package com.manage.recipe.integration;

import com.manage.recipe.RecipeManagerApplication;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import com.manage.recipe.model.dao.RecipeDAO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = RecipeManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {"recipe.host.url=http://localhost", "recipe.context.path=/recipes"})
public class RecipeManagerSearchIntegrationTests {
    @LocalServerPort
    private int port;

    @Value("${recipe.host.url}")
    private String baseUrl;

    @Value("${recipe.context.path}")
    private String context;

    private static TestRestTemplate testRestTemplate;

    @Autowired
    private RecipeManagerTestRepository recipeManagerTestRepository;

    private List<RecipeDAO> recipeDAOList = new ArrayList<>();
    private RecipeFilterSearchDTO recipeFilterSearchDTO;
    private static final String SEARCH = "/search";
    private static final String QUESTION_MARK = "?";
    private static final String QUERY_APPEND_CHARACTER = "&";
    private static final String EQUAL_CHARACTER = "=";
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
        recipeFilterSearchDTO = RecipeFilterSearchDTO.builder().build();
        baseUrl = baseUrl.concat(":").concat(port + "").concat(context);
        RecipeDAO firstRrecipeDAO = RecipeDAO.builder()
                .name("recipe1")
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("ingredient1").build(),
                        IngredientDAO.builder().ingredientName("ingredient2").build()))
                .instructions("instruction1")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();
        RecipeDAO secondRrecipeDAO = RecipeDAO.builder()
                .name("recipe2")
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("ingredient3").build(),
                        IngredientDAO.builder().ingredientName("ingredient4").build()))
                .instructions("instruction2")
                .foodCategory(FoodCategory.UNKOWN)
                .servings(2)
                .build();
        recipeDAOList.add(firstRrecipeDAO);
        recipeDAOList.add(secondRrecipeDAO);
        recipeManagerTestRepository.saveAll(recipeDAOList);
    }

    @AfterEach
    public void tearDown() {
        recipeManagerTestRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllVegetarianFoodList() {
        String foodCategory = "VEG";
        RecipeResponseDTO response = testRestTemplate.getForObject(
                baseUrl + SEARCH + QUESTION_MARK + FOOD_CATEGORY_SEARCH + EQUAL_CHARACTER + foodCategory,
                RecipeResponseDTO.class,
                recipeFilterSearchDTO);

        Assertions.assertEquals(1, response.getRecipeDTOList().size());
    }

    @Test
    public void shouldSearchForServingsAndIncludedIngredients() {
        int numberOfServings = 1;
        String includeIngredient = "ingredient1";
        RecipeResponseDTO response = testRestTemplate.getForObject(
                baseUrl
                        + SEARCH
                        + QUESTION_MARK
                        + SERVINGS_SEARCH
                        + EQUAL_CHARACTER
                        + numberOfServings
                        + QUERY_APPEND_CHARACTER
                        + INCLUDE_INGREDIENT_SEARCH
                        + includeIngredient,
                RecipeResponseDTO.class,
                recipeFilterSearchDTO);
        Assertions.assertEquals(1, response.getRecipeDTOList().size());
    }

    @Test
    public void shouldSearchForExcludedIngredientsWithInstructionSearch() {
        String excludeIngredientSearch = "ingredient5";
        String searchTextInInstruction = "instruction1";
        RecipeResponseDTO response = testRestTemplate.getForObject(
                baseUrl
                        + SEARCH
                        + QUESTION_MARK
                        + EXCLUDE_INGREDIENT_SEARCH
                        + excludeIngredientSearch
                        + QUERY_APPEND_CHARACTER
                        + INSTRUCTION_TEXT_SEARCH
                        + EQUAL_CHARACTER
                        + searchTextInInstruction,
                RecipeResponseDTO.class,
                recipeFilterSearchDTO);
        Assertions.assertEquals(1, response.getRecipeDTOList().size());
    }
}
