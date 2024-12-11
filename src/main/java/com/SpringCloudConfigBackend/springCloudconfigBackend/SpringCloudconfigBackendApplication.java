package com.SpringCloudConfigBackend.springCloudconfigBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class SpringCloudconfigBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudconfigBackendApplication.class, args);
	}

}
