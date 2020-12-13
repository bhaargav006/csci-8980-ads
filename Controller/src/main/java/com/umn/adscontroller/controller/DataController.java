package com.umn.adscontroller.controller;

import com.umn.adscontroller.service.DataService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DataController {

    Logger apiLogger = LoggerFactory.getLogger("api-trace");

    private DataService dataService;

    @Autowired
    DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/{key}")
    public ResponseEntity<String> getData(@PathVariable("key") String inputKey) {
        long startTime = System.nanoTime();

        String value = dataService.getData(inputKey);
        if (value == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        JSONObject response = new JSONObject(value);

        long endTime = System.nanoTime();
        apiLogger.info("GET {} {} {}", inputKey, (endTime - startTime), response.getBoolean("value"));
        return new ResponseEntity<>(response.getString("key"), HttpStatus.OK);
    }

    @PutMapping(value = "/{key}")
    public ResponseEntity<String> putData(@PathVariable("key") String inputKey, @RequestBody String inputValue) {
        long startTime = System.nanoTime();

        String value = dataService.putData(inputKey, inputValue);

        JSONObject response = new JSONObject(value);

        long endTime = System.nanoTime();
        apiLogger.info("PUT {} {} {}", inputKey, (endTime - startTime), response.getBoolean("value"));
        return new ResponseEntity<String>(inputKey, HttpStatus.CREATED);
    }
}
