package com.labrasaviva.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restauranteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de LA BRASA VIVA")
                        .description("API REST para gestionar la carta, pedidos y cocina del restaurante")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Equipo DOSW")
                                .email("dosw@eci.edu.co")));
    }
}
