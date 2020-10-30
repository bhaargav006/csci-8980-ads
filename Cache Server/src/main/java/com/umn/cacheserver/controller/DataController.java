package com.umn.cacheserver.controller;

import com.umn.cacheserver.model.CacheData;
import com.umn.cacheserver.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {

    @GetMapping(value = "/{key}")
    public String getData(@PathVariable("key") String inputKey) {

        return "Hello! Data is: " + inputKey;
    }
}
