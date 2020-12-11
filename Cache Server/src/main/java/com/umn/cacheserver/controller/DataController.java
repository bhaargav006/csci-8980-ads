package com.umn.cacheserver.controller;

import com.umn.cacheserver.service.CacheService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DataController {

    private final CacheService cacheService;

    @Autowired
    DataController(CacheService cacheService){
        this.cacheService = cacheService;
    }

    @GetMapping(value = "/{key}")
    public Pair<String, Boolean> getData(@PathVariable("key") String inputKey) {
        Pair<String, Boolean> value = cacheService.getValue(inputKey);
        return value;
    }

    @PutMapping(value = "/{key}")
    public ResponseEntity<String> putData(@PathVariable("key") String inputKey, @RequestBody String inputValue) {
        cacheService.postValue(inputKey, inputValue);
        return new ResponseEntity<String>(inputKey, HttpStatus.CREATED);
    }
}
