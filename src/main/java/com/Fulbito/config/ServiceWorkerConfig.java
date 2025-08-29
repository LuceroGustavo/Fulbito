package com.Fulbito.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ServiceWorkerConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🚀 CONFIGURACIÓN ESPECÍFICA PARA SERVICE WORKER
        registry.addResourceHandler("/sw.js")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0); // Sin cache para Service Worker
        
        // 🚀 CONFIGURACIÓN PARA MANIFEST.JSON
        registry.addResourceHandler("/manifest.json")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }
}
