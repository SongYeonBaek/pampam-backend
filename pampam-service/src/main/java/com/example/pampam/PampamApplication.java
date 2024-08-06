package com.example.pampam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PampamApplication {

    public static void main(String[] args) {
        SpringApplication.run(PampamApplication.class, args);
    }

}
