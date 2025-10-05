package com.spring.NetFortify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // turns on the springs asyncronus method execution capability
// any public method with @Async ,instead of running it in the main thread it submits to a background thread pool for execution
//when we call an async method Spring request a thread from its managed thread pool
// core pool size 8 threads ,maximum pool size unlimited
// we can even configure this thread pool

public class NetFortifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetFortifyApplication.class, args);
	}

}
