package com.crozhere.service.cms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crozhere Club Management System API")
                        .description("API documentation for the Crozhere Club Management System. " +
                                "This API provides endpoints for managing clubs, stations, bookings, players, and layouts.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Shishank Sharma")
                                .email("shishanksharma6@gmail.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://crozhere.com")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Default Server URL")
                ));
    }
} 