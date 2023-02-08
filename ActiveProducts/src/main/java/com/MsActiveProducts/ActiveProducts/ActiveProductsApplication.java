package com.MsActiveProducts.ActiveProducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaClient
public class ActiveProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActiveProductsApplication.class, args);
	}

}
