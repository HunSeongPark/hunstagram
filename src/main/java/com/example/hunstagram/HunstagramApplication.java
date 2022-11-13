package com.example.hunstagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HunstagramApplication {

    public static void main(String[] args) {
        SpringApplication.run(HunstagramApplication.class, args);
    }

}
