package com.ufpb.br.apps4society.my_trace_table_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI(){
        return new OpenAPI()
                .info(info());
    }

    private Info info() {
        return new Info()
                .title("API para o My-Trace-Table")
                .description("Desenvolvida com Java 21 e Spring Boot 3, essa API tem como objetivo ajudar no aprendizado de lógica de programação")
                .version("v1.0.0")
                .license(new License()
                        .name("Apache License 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }


}
