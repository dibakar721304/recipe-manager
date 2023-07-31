package com.manage.recipe.handler;

import com.manage.recipe.exception.InvalidRecipeRequestException;
import com.manage.recipe.exception.RecipeNotFoundException;
import com.manage.recipe.model.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RecipeManagerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RecipeManagerExceptionHandler.class);

    @ExceptionHandler(InvalidRecipeRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRecipeRequestException(
            InvalidRecipeRequestException invalidRecipeRequestException) {
        logger.error("InvalidRecipeRequestException occurred {}", invalidRecipeRequestException.getMessage());
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error(invalidRecipeRequestException.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRecipeNotFoundException(
            RecipeNotFoundException recipeNotFoundException) {
        logger.error("RecipeNotFoundException occurred {}", recipeNotFoundException.getMessage());

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .error(recipeNotFoundException.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleAllUncaughtException(RuntimeException runtimeException) {
        logger.error("RuntimeException occurred {}", runtimeException.getMessage());
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(runtimeException.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
