package com.m4l0n.hitchride.configuration;

// Programmer's Name: Ang Ru Xian
// Program Name: SwaggerConfig.java
// Description: This is a class that configures the Swagger API
// Last Modified: 22 July 2023

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Hitchride API",
        version = "1.0",
        description = "Documentation Hitchride API v1.0",
        contact = @Contact(name = "m4l0n")),
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SwaggerConfig {

}