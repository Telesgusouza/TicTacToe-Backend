package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("https://nimble-swan-c8809e.netlify.app", "http://localhost:5173")
            .allowedMethods("*") // Permita todos os métodos
            .allowedHeaders("*") // Permita todos os cabeçalhos
            .allowCredentials(true)
            .maxAge(3600); // Adicione um tempo máximo de cache
    }

}
