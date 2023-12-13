package com.manage.recipe.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record AuthRequestDTO(
        @Schema(example = "admin", description = "this field is  used to pass username") String userName,
        @Schema(example = "admin", description = "this field is  used to pass password") String password) {}
