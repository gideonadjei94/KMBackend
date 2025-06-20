package com.gideon.knowmate.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "KnowMate Api Docs",
                description = "Backend Api docs for KnowMate",
                version = "1.0"
        ),
        servers = {
              @Server(
                      description = "LOCAL ENV",
                      url = "http://localhost:8090"
              ),

                @Server(
                        description = "PROD ENV",
                        url = "https://knowmatebackend.onrender.com"
                )

        }
)

@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
