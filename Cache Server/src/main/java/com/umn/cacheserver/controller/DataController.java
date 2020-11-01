package com.umn.cacheserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DataController {

    @GetMapping(value = "/{key}")
    public String getData(@PathVariable("key") String inputKey) {
        return "Hello! Data is: " + inputKey;
    }

    @PutMapping(value = "/{key}")
    public ResponseEntity<String> putData(@PathVariable("key") String inputKey, @RequestBody String inputValue) {
        return new ResponseEntity<String>(inputKey, HttpStatus.CREATED);
    }
}
