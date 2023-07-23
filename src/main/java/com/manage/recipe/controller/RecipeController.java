package com.manage.recipe.controller;

import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("recipes")
@Validated
public class RecipeController {
    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(summary = "Add a new recipe")
    @ApiResponse(responseCode = "202", description = "Recipe added")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping(value = "/add")
    public ResponseEntity<RecipeDTO> addNewRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        logger.debug("Add new recipe with name {}", recipeDTO.getName());
        RecipeDTO responseRecipeDTO = recipeService.addNewRecipe(recipeDTO);
        logger.debug("New recipe added with name {}", responseRecipeDTO.getName());
        return new ResponseEntity<>(responseRecipeDTO, HttpStatus.CREATED);
    }
}
