package com.manage.recipe.controller;

import com.manage.recipe.model.dto.RecipeDTO;
import com.manage.recipe.model.dto.RecipeFilterSearchDTO;
import com.manage.recipe.model.dto.RecipeResponseDTO;
import com.manage.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Slf4j
@Validated
public class RecipeController {
    // private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

    @Value("${recipe.page.pageSize:10}")
    private int pageSize;

    @Value("${recipe.page.sortDirection:DESC}")
    private String sortDirection;

    @Value("${recipe.page.defaultSort:id}")
    private String defaultSort;

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
        log.debug("Add new recipe with name {}", recipeDTO.getName());
        RecipeDTO responseRecipeDTO = recipeService.addNewRecipe(recipeDTO);
        log.debug("New recipe added with name {}", responseRecipeDTO.getName());
        return new ResponseEntity<>(responseRecipeDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Fetch all recipes")
    @ApiResponse(responseCode = "200", description = "Recipes fetched")
    @ApiResponse(responseCode = "404", description = "Recipes not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<RecipeResponseDTO> fetchAllRecipes() {
        log.info("Request for fetching all  recipes");
        RecipeResponseDTO allRecipes = recipeService.fetchAllRecipes();
        return new ResponseEntity<>(allRecipes, HttpStatus.OK);
    }

    @Operation(summary = "Fetch recipe by recipe id")
    @ApiResponse(responseCode = "200", description = "Recipe fetched")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping(value = "/id")
    public ResponseEntity<RecipeDTO> fetchRecipe(@RequestParam Long recipeId) {
        log.debug("Request for fetching  recipe with id {}", recipeId);
        RecipeDTO recipe = recipeService.fetchRecipeById(recipeId);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @Operation(summary = "Update recipe")
    @ApiResponse(responseCode = "200", description = "Recipe updated")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PutMapping(value = "/update/{recipeId}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeDTO recipeDTO) {
        log.debug("Updating recipe with id {}", recipeId);
        RecipeDTO updatedRecipe = recipeService.updateRecipe(recipeId, recipeDTO);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @Operation(summary = "Remove recipe by id")
    @ApiResponse(responseCode = "204", description = "Recipe removed")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping(value = "/delete/{recipeId}")
    public ResponseEntity<Void> removeRecipe(@PathVariable Long recipeId) {
        log.warn("Removing recipe with id {}", recipeId);
        recipeService.removeRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search recipes")
    @ApiResponse(responseCode = "204", description = "Recipes returned")
    @ApiResponse(responseCode = "404", description = "Recipe not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/search")
    public ResponseEntity<RecipeResponseDTO> searchRecipes(
            RecipeFilterSearchDTO recipeFilterSearchDTO, @RequestParam(required = false, defaultValue = "0") int page) {
        log.debug("Recipe search request {}", recipeFilterSearchDTO);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.valueOf(sortDirection), defaultSort);
        return new ResponseEntity<>(recipeService.searchRecipes(recipeFilterSearchDTO, pageable), HttpStatus.OK);
    }
}
