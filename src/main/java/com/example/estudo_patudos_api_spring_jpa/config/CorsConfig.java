package com.example.estudo_patudos_api_spring_jpa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS global e parametrizavel por ambiente.
 *
 * Origens permitidas vem da variavel CORS_ALLOWED_ORIGINS (lista separada por virgula).
 * Default: http://localhost:5173 (front Vite local).
 * Aceita padroes (ex.: https://*.vercel.app) para cobrir producao + previews do Vercel.
 *
 * Exposto como CorsConfigurationSource para o Spring Security usar uma unica fonte
 * de CORS (via http.cors()), evitando headers duplicados.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
