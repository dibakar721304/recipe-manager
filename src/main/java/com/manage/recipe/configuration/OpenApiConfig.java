package com.manage.recipe.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @SecurityScheme(
//        name = "in-memory",
//        type = SecuritySchemeType.APIKEY,
//        bearerFormat = "JWT",
//        scheme = "Bearer",
//        paramName = "Authorization",
//        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

    @Bean
    public OpenAPI recipeManagerOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("in-memory"))
                .components(new Components()
                        .addSecuritySchemes(
                                "in-memory",
                                new SecurityScheme()
                                        .name("in-memory")
                                        .in(SecurityScheme.In.HEADER)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .bearerFormat("JWT")
                                        .scheme("Bearer")))
                .info(new Info()
                        .title("Favorite recipe manager service")
                        .description("This rest Api allows users to manage their favourite recipes")
                        .version("v1.0")
                        .contact(new Contact().name("Test"))
                        .termsOfService("TOC")
                        .license(new License().name("License").url("#")));
    }
}
