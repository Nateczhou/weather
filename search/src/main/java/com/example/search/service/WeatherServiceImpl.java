package com.example.search.service;


import com.example.search.config.EndpointConfig;
import com.example.search.pojo.City;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Completable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WeatherServiceImpl implements WeatherService{


    private final RestTemplate internalRestTemplate;

    private final RestTemplate externalRestTemplate;

    // thread pool
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public WeatherServiceImpl(RestTemplate internalRestTemplate, RestTemplate externalRestTemplate) {
        this.internalRestTemplate = internalRestTemplate;
        this.externalRestTemplate = externalRestTemplate;
    }

    //    @Override
//    @Retryable(include = IllegalAccessError.class)
//    public List<List<Integer>> findCityIdByName(List<String> city) throws ExecutionException, InterruptedException {
//        List<CompletableFuture<List<Integer>>> allFutures = new ArrayList<>();
//        List<List<Integer>> list = new ArrayList<>();
//        for (String c: city) {
//            CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() -> {
//                List<Integer> temp = helper(c);
//                list.add(temp);
//                return temp;
//            }, executor);
//            allFutures.add(future);
//        }
//
//        CompletableFuture<Void> allOf = CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0]));
//        allOf.get();
//        return list;
//
//    }
//
//    private List<Integer> helper(String city) {
//        City[] cities = restTemplate.getForObject(EndpointConfig.queryWeatherByCity + city, City[].class);
//        List<Integer> ans = new ArrayList<>();
//        for(City c: cities) {
//            if(c != null && c.getWoeid() != null) {
//                ans.add(c.getWoeid());
//            }
//        }
//        return ans;
//    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public List<Map<String, Map>> findWeatherByCity(List<String> city) throws ExecutionException, InterruptedException {
        List<CompletableFuture<List<Integer>>> allFutures = new ArrayList<>();
        List<Map<String, Map>> list = new ArrayList<>();
        for (String c: city) {
            CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() -> {
                List<Integer> temp = helper(c);
                for (int id: temp) {
                    list.add(findWeatherById(id));
                }
                return temp;
            }, executor);
            allFutures.add(future);
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0]));
        allOf.get();
        return list;
    }

    public List<Integer> helper(String name) {
        List<Integer> res = internalRestTemplate.getForObject("http://detail-service/detail?city="+name, ArrayList.class);
        return res;
    }


    @Override
    public Map<String, Map> findWeatherById(int id) {
        Map<String, Map> ans = externalRestTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
        System.out.println(ans);
        return ans;
    }
}


/**
 *  -> gateway -> eureka
 *       |
 *   weather-search -> hystrix(thread pool) -> 3rd party weather api
 *
 *
 *  circuit breaker(hystrix)
 * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *
 *   weather-search service should get city id from detail service
 *   and use multi-threading to query city's weather details
 *
 *   gateway
 *     |
 *  weather-service -> 3rd party api(id <-> weather)
 *    |
 *  detail-service -> 3rd party api (city <-> id)
 *
 *  failed situations:
 *      1. 3rd party api timeout -> retry + hystrix
 *      2. 3rd party api available time / rate limit
 *      3. security verification
 *  response
 *      1. no id -> error / empty
 *      2. large response -> pagination / file download (link / email)
 *  performance
 *      1. cache / db
 *
 *   gateway
 *     |
 *  weather-service -> cache(city - id - weather) (LFU)
 *    |
 *   DB (city - id - weather) <-> service <->  message queue  <-> scheduler <-> 3rd party api(city - id)
 *                                                                  |
 *                                                         update id - weather every 30 min
 *                                                         update city - id relation once per day
 *
 *  homework :
 *      deadline -> Wednesday midnight
 *      1. update detail service
 *          a. send request to 3rd party api -> get id by city
 *      2. update search service
 *          a. add ThreadPool
 *          b. send request to detail service -> get id by city
 *          c. use CompletableFuture send request to 3rd party api -> get weather by ids
 *          d. add retry feature
 */