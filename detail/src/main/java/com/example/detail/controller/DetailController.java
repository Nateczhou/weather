package com.example.detail.controller;

import com.example.detail.pojo.City;
import com.example.detail.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class DetailController {

    private DetailService detailService;

    @Value("${server.port}")
    private int serverPort;


    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

    @GetMapping("/detail/hello")
    public ResponseEntity<?> test() {
        return new ResponseEntity<>("hello world", HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getCityById(@PathVariable String id) {
        return new ResponseEntity<>("testing inside of detail /deatil/{id} controller, port: "+serverPort, HttpStatus.OK);
    }

    @GetMapping("/detail")
    public ResponseEntity<List<Integer>> getCityIdByName(@RequestParam String city) throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(detailService.findCityIdByName(city), HttpStatus.OK);
    }

    @GetMapping("/port")
    public ResponseEntity<?> getDetails() {
        return new ResponseEntity<>("detail service port is " + serverPort, HttpStatus.OK);
    }
}
