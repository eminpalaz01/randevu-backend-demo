package com.dershaneproject.randevu.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	
	    // BURAYA TEKRAR BAK.
		@Bean
		public WebMvcConfigurer corsConfigurer() {
			return new WebMvcConfigurer() {
				
				@Override
				public void addCorsMappings(CorsRegistry registery) {
					registery.addMapping("/**")
					.allowedMethods("*")
					.allowedOrigins("*") // Burada birden fazla adres girilebilir mi?
					.allowedHeaders("*")
					.allowCredentials(false)
					.maxAge(-1);
					
				}
				
				
				
			};
			
		}

}
