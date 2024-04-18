package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@ComponentScan("application")
//@EnableJpaRepositories("practicum")
@SpringBootApplication
public class StatServerApp {
    public static void main(String[] args) {
        SpringApplication.run(StatServerApp.class, args);
    }
}
