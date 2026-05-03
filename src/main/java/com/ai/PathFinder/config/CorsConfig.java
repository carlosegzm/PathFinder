package com.ai.PathFinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração global de CORS (Cross-Origin Resource Sharing) para a aplicação.
 * Esta classe permite que recursos da API sejam acessados por aplicações front-end 
 * hospedadas em domínios diferentes, como ambientes de desenvolvimento local ou produção na Vercel.
 */
@Configuration
public class CorsConfig {

    /**
     * Define as regras de mapeamento CORS para os endpoints da API.
     * Configura as origens permitidas, os métodos HTTP aceites (GET, POST, etc.) 
     * e os cabeçalhos autorizados.
     * 
     * @return Uma instância de {@link WebMvcConfigurer} com as definições de CORS aplicadas.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("http://localhost:5173", "https://*.vercel.app")
                        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }
}
