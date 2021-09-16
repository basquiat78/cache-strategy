package io.basquiat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CacheStrategyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheStrategyApplication.class, args);
	}

}
