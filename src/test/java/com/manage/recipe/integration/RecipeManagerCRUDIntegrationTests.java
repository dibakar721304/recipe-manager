package com.manage.recipe.integration;

import com.manage.recipe.RecipeManagerApplication;
import com.manage.recipe.model.FoodCategory;
import com.manage.recipe.model.dao.IngredientDAO;
import com.manage.recipe.model.dao.RecipeDAO;
import com.manage.recipe.model.dto.AuthRequestDto;
import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = RecipeManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {"recipe.host.url=http://localhost", "recipe.context.path=/recipes"})
class RecipeManagerCRUDIntegrationTests {

    @LocalServerPort
    private int port;

    @Value("${recipe.host.url}")
    private String baseUrl;

    @Value("${recipe.context.path}")
    private String context;

    private static TestRestTemplate testRestTemplate;

    @Autowired
    private RecipeManagerTestRepository recipeManagerTestRepository;

    private RecipeDAO recipeDAO;

    @BeforeAll
    public static void init() {
        testRestTemplate = new TestRestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat(context);
        recipeDAO = RecipeDAO.builder()
                .name("recipe1")
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("ingredient1").build(),
                        IngredientDAO.builder().ingredientName("ingredient2").build()))
                .instructions("instruction1")
                .foodCategory(FoodCategory.VEG)
                .servings(1)
                .build();
        recipeManagerTestRepository.saveAndFlush(recipeDAO);
        List<RecipeDAO> recipeDAOList = recipeManagerTestRepository.findAll();
        recipeDAO = recipeDAOList.get(0);
    }

    @AfterEach
    public void tearDown() {
        recipeManagerTestRepository.deleteAll();
    }

    @Test
    public void testAuthenication() {
        AuthRequestDto authRequestDto = new AuthRequestDto("admin", "admin");

        // Act
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                "http://localhost:" + port + "/auth/v1/login", authRequestDto, Map.class);
        // ResponseEntity<Map<String, String>> response =
        // testRestTemplate.postForObject("http://localhost:"+port+"/auth/v1/login", authRequestDto, String.class);

        Map<String, String> responseBody = response.getBody();
        // assertThat(responseBody).containsKey("token");
        // Assert
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
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
        HttpHeaders headers = new HttpHeaders();

        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + generateAuthToken("admin"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);

        RecipeDTO response = testRestTemplate.postForObject(baseUrl, recipeDTO, RecipeDTO.class);
        assert response != null;
        Assertions.assertEquals("testRecipe", response.getName());
    }

    private static final String SECRET_KEY = "yourSecretKey";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public static String generateAuthToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    @Test
    public void testFetchAllRecipesWhenRecipesArePresent() {
        RecipeResponseDTO response = testRestTemplate.getForObject(baseUrl, RecipeResponseDTO.class);
        Assertions.assertEquals(1, response.getRecipeDTOList().size());
    }

    @Test
    public void testThrowExceptionWhenRecipesAreNotPresent() {
        recipeManagerTestRepository.deleteAll();
        var response = testRestTemplate.getForObject(baseUrl, RecipeResponseDTO.class);
        Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    public void testFetchRecipeById() {
        List<RecipeDAO> recipeDAOList = recipeManagerTestRepository.findAll();
        recipeDAO = recipeDAOList.get(0);
        RecipeDTO response = testRestTemplate.getForObject(baseUrl + "/id/" + recipeDAO.getId(), RecipeDTO.class);
        Assertions.assertEquals("recipe1", response.getName());
    }

    @Test
    public void testThrowExceptionIfRecipeNotFoundbyId() {
        recipeManagerTestRepository.deleteAll();
        var response = testRestTemplate.getForObject(baseUrl + "/id/1", RecipeDTO.class);

        Assertions.assertNull(response.getName());
    }

    @Test
    public void testUpdateRecipeById() {
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList(
                        IngredientDAO.builder().ingredientName("salt").build(),
                        IngredientDAO.builder().ingredientName("sugar").build()))
                .name("updatedRecipe")
                .foodCategory(FoodCategory.UNKOWN)
                .servings(1)
                .instructions("Test updated instruction for recipe")
                .build();
        ResponseEntity<RecipeDTO> response = testRestTemplate.exchange(
                baseUrl + "/update/" + recipeDAO.getId(), HttpMethod.PUT, new HttpEntity<>(recipeDTO), RecipeDTO.class);

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
        HttpEntity<RecipeDTO> requestEntity = new HttpEntity<>(recipeDTO, headers);
        var putResponseEntity =
                testRestTemplate.exchange(baseUrl + "/update/5", HttpMethod.PUT, requestEntity, RecipeDTO.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, putResponseEntity.getStatusCode());
    }

    //    @Test
    //    public void testDeleteRecipe() {
    //        var putResponseEntity = testRestTemplate.exchange(
    //                baseUrl + "/delete/" + recipeDAO.getId(), HttpMethod.DELETE, null, Void.class);
    //
    //        Assertions.assertEquals(HttpStatus.NO_CONTENT, putResponseEntity.getStatusCode());
    //    }
    //
    //    @Test
    //    public void testThrowExceptionWhenDeleteRecipeByInvalidId() {
    //
    //        var putResponseEntity = testRestTemplate.exchange(baseUrl + "/delete/5", HttpMethod.DELETE, null,
    // Void.class);
    //
    //        Assertions.assertEquals(HttpStatus.NOT_FOUND, putResponseEntity.getStatusCode());
    //    }
}
