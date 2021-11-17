package com.example.search.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public interface WeatherService {
    List<Map<String, Map>> findWeatherByCity(List<String> city) throws ExecutionException, InterruptedException;
    Map<String, Map> findWeatherById(int id);

}
