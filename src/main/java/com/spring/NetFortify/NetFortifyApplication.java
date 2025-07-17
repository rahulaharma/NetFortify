package com.spring.NetFortify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NetFortifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetFortifyApplication.class, args);
	}

}
