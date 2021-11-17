package com.example.detail.service;

import com.example.detail.config.EndpointConfig;
import com.example.detail.pojo.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DetailServiceImpl implements DetailService{
    @Autowired
    private final RestTemplate restTemplate;

    // thread pool need fix later, since every new detail service will have a new thread pool
    public static ExecutorService executor = Executors.newFixedThreadPool(10);


    public DetailServiceImpl(RestTemplate getRestTemplate) {
        this.restTemplate = getRestTemplate;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public List<Integer> findCityIdByName(String city) throws ExecutionException, InterruptedException {
        City[] cities = restTemplate.getForObject(EndpointConfig.queryWeatherIdByCity + city, City[].class);
        List<Integer> ans = new ArrayList<>();
        for(City c: cities) {
            if(c != null && c.getWoeid() != null) {
                ans.add(c.getWoeid());
            }
        }
        return ans;

    }
}
