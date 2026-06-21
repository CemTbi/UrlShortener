package io.github.cemtbi.urlshortener.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Every Endpoint
                        .allowedOrigins(
                            "http://localhost:3000",      // Local Testing
                            "https://cemtbi.github.io/UrlShortenerWeb/" 
                        )
                        .allowedMethods("GET", "POST", "OPTIONS") 
                        .allowedHeaders("*") 
                        .allowCredentials(true) 
                        .maxAge(3600); 
            }
        };
    }
}
