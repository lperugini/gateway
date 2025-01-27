package com.sap.periziafacile.pfgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.sap.periziafacile.pfgateway")
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

}
