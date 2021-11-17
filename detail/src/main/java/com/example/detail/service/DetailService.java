package com.example.detail.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public interface DetailService {
    List<Integer> findCityIdByName(String city) throws ExecutionException, InterruptedException;
}
