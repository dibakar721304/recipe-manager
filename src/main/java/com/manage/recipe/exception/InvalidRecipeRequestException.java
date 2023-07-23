package com.manage.recipe.exception;

public class InvalidRecipeRequestException extends RuntimeException {

    public InvalidRecipeRequestException(String message) {
        super(message);
    }
}
