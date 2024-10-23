package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocOpenApiConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("REST api - Tic Tac Toe")
						.description("an API to be able to try the Tic Tac Toe game online").version("1")
						
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0"))
						
						.contact(new Contact()
							.name("Gustavo Teles")
							.email("gustavo.teles711@gmail.com")
							
							.url("linkeding: https://www.linkedin.com/in/gustavo-teles-270a711a7/")
							.url("github: https://github.com/Telesgusouza")
							.url("portifólio: https://deluxe-daifuku-effce8.netlify.app/")));
	}

}
