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
    public ResponseEntity<Pair<String, Boolean>> getData(@PathVariable("key") String inputKey) {
        Pair<String, Boolean> value = cacheService.getValue(inputKey);
        if (value == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    @PostMapping(value = "/{key}")
    public ResponseEntity<Pair<String, Boolean>> putData(@PathVariable("key") String inputKey, @RequestBody String inputValue) {
        Pair<String, Boolean> value = cacheService.postValue(inputKey, inputValue);
        return new ResponseEntity<>(value, HttpStatus.CREATED);
    }
}
