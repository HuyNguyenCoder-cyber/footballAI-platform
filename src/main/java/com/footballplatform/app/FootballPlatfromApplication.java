package com.footballplatform.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FootballPlatfromApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballPlatfromApplication.class, args);
    }

}
