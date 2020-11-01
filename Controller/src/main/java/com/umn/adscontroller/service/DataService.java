package com.umn.adscontroller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DataService {

    private RestTemplate restTemplate;

    DataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getData(String inputKey) {
        // TODO: Implement hashing function to get URL

        return restTemplate.getForObject("http://localhost:8081/api/" + inputKey, String.class);
    }

    public void putData(String inputKey, String inputValue) {
        // TODO: Implement hashing function to get URL

        restTemplate.put("http://localhost:8081/api/" + inputKey, inputValue);
    }
}
