package com.crozhere.service.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // applies to all endpoints
                .allowedOrigins("http://localhost:3000") // your Next.js frontend
                .allowedMethods("*") // GET, POST, etc.
                .allowedHeaders("*"); // allow all headers
    }
}
