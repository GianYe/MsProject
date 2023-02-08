package com.MsPassiveProducts.PassiveProducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaClient
public class PassiveProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PassiveProductsApplication.class, args);
	}
}
