package com.prestamos.sistema_prestamos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SistemaPrestamosApplication {
	public static void main(String[] args) {
		SpringApplication.run(SistemaPrestamosApplication.class, args);
	}
}