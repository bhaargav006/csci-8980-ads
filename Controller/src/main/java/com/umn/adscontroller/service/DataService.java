package com.umn.adscontroller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DataService {

    private RestTemplate restTemplate;

    DataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${controller.server.list}")
    private List<String> arrayOfStrings;


    public String getData(String inputKey) {
        int serverId = computeHash(inputKey);

        String baseUrl = arrayOfStrings.get(serverId);
        String url = String.format("http://%s/api/%s", baseUrl, inputKey);
        
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (HttpStatusCodeException exception) {
            return null;
        }
    }

    public String  putData(String inputKey, String inputValue) {
        int serverId = computeHash(inputKey);

        String baseUrl = arrayOfStrings.get(serverId);
        String url = String.format("http://%s/api/%s", baseUrl, inputKey);

        return restTemplate.postForObject(url, inputValue, String.class);
    }

    private int computeHash(String inputKey) {
        int p = 53;
        double m = 1e9 + 9;
        double hashValue = 0;
        double pPow = 1;

        for (char c : inputKey.toCharArray()) {
            hashValue = (hashValue + c * pPow) % m;
            pPow = (pPow * p) % m;
        }

        return (int) hashValue % arrayOfStrings.size();
    }
}
