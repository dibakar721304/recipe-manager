package com.manage.recipe.controller;

import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * This controller class holds the rest api end point logic for recipe manager service.
 * @author AnantDibakar
 * @date 26/07/2023
 */
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
    @PostMapping
    public ResponseEntity<RecipeDTO> addNewRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        logger.debug("Add new recipe with name {}", recipeDTO.getName());
        RecipeDTO responseRecipeDTO = recipeService.addNewRecipe(recipeDTO);
        logger.debug("New recipe added with name {}", responseRecipeDTO.getName());
        return new ResponseEntity<>(responseRecipeDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch all recipes")
    @ApiResponse(responseCode = "200", description = "Recipes fetched")
    @ApiResponse(responseCode = "404", description = "Recipes not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<RecipeResponseDTO> fetchAllRecipes() {
        logger.debug("Request for fetching all  recipes");
        RecipeResponseDTO allRecipes = recipeService.fetchAllRecipes();
        return new ResponseEntity<>(allRecipes, HttpStatus.OK);
    }

    @Operation(summary = "Fetch recipe by recipe id")
    @ApiResponse(responseCode = "200", description = "Recipe fetched")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping(value = "/id/{recipeId}")
    public ResponseEntity<RecipeDTO> fetchRecipe(@PathVariable Long recipeId) {
        logger.debug("Request for fetching  recipe");
        RecipeDTO recipe = recipeService.fetchRecipeById(recipeId);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @Operation(summary = "Update recipe")
    @ApiResponse(responseCode = "200", description = "Recipe updated")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PutMapping(value = "/update/{recipeId}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeDTO recipeDTO) {
        logger.debug("Updating recipe with id {}", recipeId);
        RecipeDTO updatedRecipe = recipeService.updateRecipe(recipeId, recipeDTO);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @Operation(summary = "Remove recipe by id")
    @ApiResponse(responseCode = "204", description = "Recipe removed")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping(value = "/delete/{recipeId}")
    public ResponseEntity<Void> removeRecipe(@PathVariable Long recipeId) {
        logger.debug("Removing recipe with id {}", recipeId);
        recipeService.removeRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filter recipes")
    @ApiResponse(responseCode = "204", description = "Recipes returned")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/search")
    public ResponseEntity<RecipeResponseDTO> searchRecipes(
            @RequestBody RecipeFilterSearchDTO searchDTO,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(recipeService.searchRecipes(searchDTO, pageable), HttpStatus.OK);
    }
}
