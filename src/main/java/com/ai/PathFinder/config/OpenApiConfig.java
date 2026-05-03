package com.ai.PathFinder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do SpringDoc/OpenAPI para a geração automática da documentação da API.
 * Responsável por configurar a interface do Swagger UI, facilitando o teste 
 * e a visualização dos endpoints pelos desenvolvedores.
 */
@Configuration
public class OpenApiConfig {

    // Documentação disponível em http://localhost:8080/swagger-ui.html
    
    /**
     * Cria e customiza o objeto OpenAPI que define as metainformações da documentação.
     * Configura o título da API, a versão atual e uma breve descrição das funcionalidades.
     *  
     * @return Uma instância de {@link OpenAPI} com as informações gerais da PathFinder API.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PathFinder API")
                        .version("1.0.0")
                        .description("Documentação dos endpoints da API"));
    }

}