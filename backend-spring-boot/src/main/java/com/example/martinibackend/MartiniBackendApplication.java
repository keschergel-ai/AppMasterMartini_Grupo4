package com.example.martinibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [MartiniBackendApplication]
 *
 * Punto de entrada principal de la aplicacion Spring Boot.
 * La anotacion @SpringBootApplication habilita la autoconfiguracion,
 * el escaneo de componentes (@Component, @Service, @Repository, @RestController)
 * y la configuracion de propiedades.
 */
@SpringBootApplication
public class MartiniBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MartiniBackendApplication.class, args);
    }
}
