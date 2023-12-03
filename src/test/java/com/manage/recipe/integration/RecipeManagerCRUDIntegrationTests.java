package com.manage.recipe.integration;

import com.manage.recipe.RecipeManagerApplication;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.Ingredient;
import com.manage.recipe.model.dao.Recipe;
import com.manage.recipe.model.dto.AuthRequestDTO;
import com.manage.recipe.model.dto.RecipeDTO;
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
class RecipeManagerCRUDIntegrationTests {

    @LocalServerPort
    private int port;

    @Value("${recipe.host.url:http://localhost}")
    private String baseUrl;

    @Value("${recipe.context.path:/recipes}")
    private String context;

    private static TestRestTemplate testRestTemplate;

    @Autowired
    private RecipeManagerTestRepository recipeManagerTestRepository;

    private Recipe recipe;
    private String contextUrl;
    private String token;

    @BeforeAll
    public static void init() {
        testRestTemplate = new TestRestTemplate();
    }

    @BeforeEach
    public void setUp() {
        token = getAuthenticationToken();
        contextUrl = baseUrl.concat(":").concat(port + "").concat(context);
        recipe = Recipe.builder()
                .name("recipe1")
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("ingredient1").build(),
                        Ingredient.builder().ingredientName("ingredient2").build()))
                .instructions("instruction1")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();
        recipeManagerTestRepository.saveAndFlush(recipe);
        List<Recipe> recipeList = recipeManagerTestRepository.findAll();
        recipe = recipeList.get(0);
    }

    public String getAuthenticationToken() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("admin", "admin");

        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                baseUrl.concat(":").concat(port + "") + "/auth//login", authRequestDTO, Map.class);
        Map<String, String> responseBody = response.getBody();
        return Objects.requireNonNull(responseBody).getOrDefault("token", "");
    }

    @Test
    public void testAddRecipe() {
        String recipeName = UUID.randomUUID().toString();
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("salt").build(),
                        Ingredient.builder().ingredientName("sugar").build()))
                .name(recipeName)
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
        HttpHeaders headers = new HttpHeaders();

        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);

        ResponseEntity<RecipeDTO> response = testRestTemplate.postForEntity(contextUrl, requestEntity, RecipeDTO.class);
        Assertions.assertEquals(201, response.getStatusCode().value());
        Assertions.assertEquals(
                recipeName, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void testFetchAllRecipesWhenRecipesArePresent() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        RecipeDTO recipeDTO = RecipeDTO.builder().build();
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);
        ResponseEntity<RecipeResponseDTO> response =
                testRestTemplate.exchange(contextUrl, HttpMethod.GET, requestEntity, RecipeResponseDTO.class);

        Assertions.assertTrue(
                Objects.requireNonNull(response.getBody()).getRecipeDTOList().size() > 0);
    }

    @Test
    public void testFetchRecipeById() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        RecipeDTO recipeDTO = RecipeDTO.builder().build();
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);
        Recipe recipe = recipeManagerTestRepository.findAll().stream()
                .filter(recipe1 -> recipe1.getName() != null)
                .findFirst()
                .get();
        ResponseEntity<RecipeDTO> response = testRestTemplate.exchange(
                contextUrl + "/id/" + recipe.getId(), HttpMethod.GET, requestEntity, RecipeDTO.class);

        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testThrowExceptionIfRecipeNotFoundbyId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        RecipeDTO recipeDTO = RecipeDTO.builder().build();
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);
        Recipe recipe = recipeManagerTestRepository.findAll().stream()
                .filter(recipe1 -> recipe1.getName() != null)
                .findFirst()
                .get();
        ResponseEntity<RecipeDTO> response = testRestTemplate.exchange(
                contextUrl + "/id/" + recipe.getId() + 1, HttpMethod.GET, requestEntity, RecipeDTO.class);

        Assertions.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testUpdateRecipeById() {
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        Ingredient.builder().ingredientName("salt").build(),
                        Ingredient.builder().ingredientName("sugar").build()))
                .name("updatedRecipe")
                .foodCategory(FoodCategory.UNKOWN)
                .servings(1)
                .instructions("Test updated instruction for recipe")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);
        ResponseEntity<RecipeDTO> response = testRestTemplate.exchange(
                contextUrl + "/update/" + recipe.getId(), HttpMethod.PUT, requestEntity, RecipeDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Assertions.assertEquals(
                "updatedRecipe", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void testThrowExceptionWhenUpdateRecipeByInvalidId() {
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .name("updatedRecipe")
                .foodCategory(FoodCategory.valueOf("UNKOWN"))
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);
        Recipe recipe = recipeManagerTestRepository.findAll().stream()
                .filter(recipe1 -> recipe1.getName() != null)
                .findFirst()
                .get();

        var putResponseEntity = testRestTemplate.exchange(
                contextUrl + "/update/" + recipe.getId() + 1, HttpMethod.PUT, requestEntity, RecipeDTO.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, putResponseEntity.getStatusCode());
    }

    @Test
    public void testDeleteRecipe() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Recipe recipe = recipeManagerTestRepository.findAll().stream()
                .filter(recipe1 -> recipe1.getName() != null)
                .findFirst()
                .get();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        var putResponseEntity = testRestTemplate.exchange(
                contextUrl + "/delete/" + recipe.getId(), HttpMethod.DELETE, requestEntity, Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, putResponseEntity.getStatusCode());
    }

    @Test
    public void testThrowExceptionWhenDeleteRecipeByInvalidId() {

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Recipe recipe = recipeManagerTestRepository.findAll().stream()
                .filter(recipe1 -> recipe1.getName() != null)
                .findFirst()
                .get();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        var putResponseEntity = testRestTemplate.exchange(
                contextUrl + "/delete/" + recipe.getId() + 1, HttpMethod.DELETE, requestEntity, Void.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, putResponseEntity.getStatusCode());
    }
}
