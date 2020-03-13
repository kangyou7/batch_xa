package com.glyde.mall.batch;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration(exclude={MybatisAutoConfiguration.class})
public class BatchXaApplication {

	public static void main(String[] args) {
		String jobName = null;
		String profile = System.getProperty("spring.profiles.active");

		for (String arg : args) {
			if (profile == null && arg.startsWith("--spring.profiles.active=")) {
				profile = arg.substring("--spring.profiles.active=".length());
				System.setProperty("spring.profiles.active", profile);
			}
			if (arg.startsWith("--spring.batch.job.names=")) {
				jobName = arg.substring("--spring.batch.job.names=".length());
			}
		}

		log.info(jobName);

		long start = System.currentTimeMillis();
		Runtime runtime = Runtime.getRuntime();

		SpringApplication.run(BatchXaApplication.class, args);

		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		log.info("started Spring Boot Application: {}", BatchXaApplication.class.getSimpleName());
		log.info("Spending Time in Second : " + new Double((System.currentTimeMillis() - start) / 1000));
		log.info("Usage Memory in MegaByte : " + (totalMemory - freeMemory) / 1024L * 1024L);
	}

}
