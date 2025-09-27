package com.mymate.mymate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class MymateApplication {

	@PostConstruct
	public void logApplicationStart() {
		log.info("TEST:LOG:::MymateApplication이 로드되었습니다!");
	}

	public static void main(String[] args) {
		SpringApplication.run(MymateApplication.class, args);
	}

}
