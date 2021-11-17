package com.example.detail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

//@ComponentScan(basePackages = {"com.example.common","com.example.detail"})
@SpringBootApplication
@EnableEurekaClient
public class DetailApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetailApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).build();
    }
}
