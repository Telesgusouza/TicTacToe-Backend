package com.example.demo.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
		.addMapping("/**")
		.allowedOrigins("http://localhost:5173", "https://nimble-swan-c8809e.netlify.app")
				.allowedMethods("GET", "POST", "DELETE", "HEAD", "PATCH")
				.allowedHeaders("*")
				.allowCredentials(true);
	}

}
