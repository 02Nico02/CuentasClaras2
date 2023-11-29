package com.grupo7.cuentasclaras2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.grupo7.cuentasclaras2")
public class Cuentasclaras2Application {

	public static void main(String[] args) {
		SpringApplication.run(Cuentasclaras2Application.class, args);
	}

}
