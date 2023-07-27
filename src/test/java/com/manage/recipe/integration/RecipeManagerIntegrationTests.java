package com.manage.recipe.integration;

import com.manage.recipe.RecipeManagerApplication;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import com.manage.recipe.model.dao.RecipeDAO;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import java.util.Arrays;
import javax.sql.DataSource;
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
class RecipeManagerIntegrationTests {

    @LocalServerPort
    private int port;

    @Value("${recipe.host.url}")
    private String baseUrl;

    @Value("${recipe.context.path}")
    private String context;

    @Autowired
    DataSource dataSource;

    private static TestRestTemplate testRestTemplate;

    @Autowired
    private RecipeManagerTestRepository recipeManagerTestRepository;

    @BeforeAll
    public static void init() {
        testRestTemplate = new TestRestTemplate();
    }

    @BeforeEach
    public void setUp() {
        System.out.println("datasource" + dataSource);
        baseUrl = baseUrl.concat(":").concat(port + "").concat(context);
        RecipeDAO recipeDAO = RecipeDAO.builder()
                .name("recipe1")
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("ingredient1").build(),
                        IngredientDAO.builder().ingredientName("ingredient2").build()))
                .instructions("instruction1")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();
        recipeManagerTestRepository.save(recipeDAO);
    }

    @AfterEach
    public void tearDown() {
        recipeManagerTestRepository.deleteAll();
    }

    @Test
    public void testAddRecipe() {
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("salt").build(),
                        IngredientDAO.builder().ingredientName("sugar").build()))
                .name("testRecipe")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
        RecipeDTO response = testRestTemplate.postForObject(baseUrl, recipeDTO, RecipeDTO.class);
        assert response != null;
        Assertions.assertEquals("testRecipe", response.getName());
    }

    @Test
    public void testFetchAllRecipes() {
        RecipeResponseDTO response = testRestTemplate.getForObject(baseUrl, RecipeResponseDTO.class);
        Assertions.assertEquals(1, response.getRecipeDTOList().size());
    }
}
