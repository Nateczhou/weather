package com.example.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@ComponentScan(basePackages = {"com.example.common", "com.example.search"})
@SpringBootApplication
@EnableEurekaClient
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }


//    @Bean
//    @LoadBalanced
//    public RestTemplate getRestTemplate() {
//        return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).build();
//    }
}
