package com.grupo7.cuentasclaras2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.grupo7.cuentasclaras2.interceptor.AccessLogInterceptor;

@SpringBootApplication
@ComponentScan(basePackages = "com.grupo7.cuentasclaras2")
public class Cuentasclaras2Application implements WebMvcConfigurer {

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

}
