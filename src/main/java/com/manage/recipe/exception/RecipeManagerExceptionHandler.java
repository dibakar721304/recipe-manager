package com.manage.recipe.exception;

import com.manage.recipe.model.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RecipeManagerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidRecipeRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomException(
            InvalidRecipeRequestException invalidRecipeRequestException) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(invalidRecipeRequestException.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
}
