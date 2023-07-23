package com.manage.recipe.integration;

import com.manage.recipe.model.dto.RecipeDTO;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeManagerIntegrationTests {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;
    private static String ADD_END_POINT = "/add";

    @Autowired
    private RecipeManagerTestRepository recipeManagerTestRepository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/recipes");
    }

    @Test
    public void testAddProduct() {
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .ingredients(Arrays.asList("salt", "pepper"))
                .name("testRecipe")
                .isVegetarian(true)
                .servings(1)
                .instructions("Test instruction for recipe")
                .build();
        RecipeDTO response = restTemplate.postForObject(baseUrl + ADD_END_POINT, recipeDTO, RecipeDTO.class);
        assert response != null;
        Assertions.assertEquals("testRecipe", response.getName());
        Assertions.assertEquals(1, recipeManagerTestRepository.findAll().size());
    }
}
