package com.example.sixtythreeproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SixtythreeprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SixtythreeprojectApplication.class, args);
    }

}
