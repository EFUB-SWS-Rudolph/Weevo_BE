package com.rudolph.Weevo.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI WeevoAPI() {
        Info info = new Info()
                .title("Weevo API Document")
                .description("이 문서는 Weevo 플랫폼에서 제공하는 API의 사용 방법과 주요 기능을 설명합니다.")
                .version("1.0.0");

        String jwtScheme = "jwtAuth";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);

        Components components = new Components()
                .addSecuritySchemes(jwtScheme, new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/").description("Local"))
                .components(components)
                .info(info)
                .addSecurityItem(securityRequirement);
    }
}
