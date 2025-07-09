package com.syrion.hommunity.config.openapi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hommunity - API")
                        .version("1.0.1")
                        .description("API para la aplicación Hommunity de control de acceso a zonas privadas."));
    }

    @Bean
    public OpenApiCustomizer sortSchemasAlphabetically() {
        return openApi -> Optional.ofNullable(openApi.getComponents())
                .map(Components::getSchemas)
                .ifPresent(schemas -> {
                    Map<String, Schema> sortedSchemas = schemas.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (existing, replacement) -> existing,
                                    LinkedHashMap::new
                            ));
                    openApi.getComponents().setSchemas(sortedSchemas);
                });
    }
}
