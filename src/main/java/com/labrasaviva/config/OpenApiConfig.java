package com.labrasaviva.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restauranteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de LA BRASA VIVA")
                        .description("API REST para gestionar la carta, pedidos, cuentas, reservas y parqueadero con seguridad JWT")
                        .version("v2.0")
                        .contact(new Contact()
                                .name("Equipo DOSW")
                                .email("dosw@eci.edu.co")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
