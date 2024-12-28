package com.profid.profid.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Authorization",
        bearerFormat = "jwt",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer"
)
@OpenAPIDefinition(
        info = @Info(title = "Profid Application"),
        security = @SecurityRequirement(name = "Authorization"),
        servers = {
                @Server(url = "http://localhost:8085/profid", description = "Local Server"),
        }
)
public class SwaggerConfig {
}