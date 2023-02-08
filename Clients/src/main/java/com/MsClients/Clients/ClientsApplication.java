package com.MsClients.Clients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaClient
public class ClientsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientsApplication.class, args);
	}

	@Bean
	public ReactiveRedisTemplate<String, Client> reactiveJsonPostRedisTemplate(
			ReactiveRedisConnectionFactory connectionFactory) {

		RedisSerializationContext<String, Client> serializationContext = RedisSerializationContext
				.<String, Client>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(new Jackson2JsonRedisSerializer<>(Client.class))
				.build();


		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}

}
