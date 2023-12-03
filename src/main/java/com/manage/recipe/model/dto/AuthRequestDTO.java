package com.manage.recipe.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public record AuthRequestDTO(
        @Schema(example = "admin", description = "this filed  use to pass username") String userName,
        @Schema(example = "admin", description = "this filed  use to pass password") String password) {}
