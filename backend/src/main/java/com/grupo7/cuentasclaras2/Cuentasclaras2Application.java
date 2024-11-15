package com.grupo7.cuentasclaras2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.grupo7.cuentasclaras2.filter.JWTAuthenticationFilter;
import com.grupo7.cuentasclaras2.interceptor.AccessLogInterceptor;

@SpringBootApplication
@ComponentScan(basePackages = "com.grupo7.cuentasclaras2")
public class Cuentasclaras2Application implements WebMvcConfigurer {

	@Value("${base.url}")
	private String baseUrl;

	public static void main(String[] args) {
		SpringApplication.run(Cuentasclaras2Application.class, args);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessLogInterceptor());
	}

	@Bean
	public AccessLogInterceptor accessLogInterceptor() {
		return new AccessLogInterceptor();
	}

	@Bean
	public FilterRegistrationBean<JWTAuthenticationFilter> jwtAuthenticationFilter() {
		FilterRegistrationBean<JWTAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new JWTAuthenticationFilter());
		registrationBean.addUrlPatterns("/api/*");
		return registrationBean;
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:4200")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}

	@Bean
	public String baseUrl() {
		return baseUrl;
	}

}
