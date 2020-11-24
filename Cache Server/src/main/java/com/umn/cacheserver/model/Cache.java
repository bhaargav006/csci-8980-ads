package com.umn.cacheserver.model;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Cache {

    public static int cacheSize;
    public static ConcurrentHashMap<String, Integer> lookup;
    public static List<CacheEntry> cache;

    public Cache(@Value("${cache.size}") final int cacheSize){
        cache = Collections.synchronizedList(new ArrayList<>(cacheSize));
        this.cacheSize = cacheSize;
    }

}
