package com.manage.recipe.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "in-memory",
        type = SecuritySchemeType.APIKEY,
        bearerFormat = "JWT",
        scheme = "Bearer",
        paramName = "Authorization",
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

    @Bean
    public OpenAPI recipeManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Favorite recipe manager service")
                        .description("This rest Api allows users to manage their favourite recipes")
                        .version("v1.0")
                        .contact(new Contact().name("Test"))
                        .termsOfService("TOC")
                        .license(new License().name("License").url("#")));
    }
}
