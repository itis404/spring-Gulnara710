package ru.itis.ReadMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ReadMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadMeApplication.class, args);
	}

}
