package com.sixtythree.stock63.domestic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DomesticApplication {

	public static void main(String[] args) {
		SpringApplication.run(DomesticApplication.class, args);
	}

}
