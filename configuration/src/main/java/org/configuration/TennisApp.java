package org.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.configuration", "org.infrastructure"})
public class TennisApp {
    public static void main(String[] args) {
        SpringApplication.run(TennisApp.class, args);
    }
}
