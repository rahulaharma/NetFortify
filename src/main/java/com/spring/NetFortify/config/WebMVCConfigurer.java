// File: src/main/java/com/spring/NetFortify/config/WebConfig.java
package com.spring.NetFortify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Apply CORS policy to all API endpoints
                        .allowedOrigins("*")   // Allow all origins for development
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow all common methods
                        .allowedHeaders("*");  // Allow all headers
            }
        };
    }
}
