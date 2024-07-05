package com.luckyvicky.woosan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WoosanApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoosanApplication.class, args);
		System.out.println("======================================================");
		System.out.println("CICD Test");
		System.out.println("======================================================");
	}

}
