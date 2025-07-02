package com.achcar_solutions.easycomm;

import org.springframework.boot.SpringApplication;

public class TestEasycommApplication {

	public static void main(String[] args) {
		SpringApplication.from(EasycommApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
