package com.umn.cacheserver.service;

import com.umn.cacheserver.model.Cache;
import com.umn.cacheserver.model.CacheData;
import com.umn.cacheserver.model.CacheEntry;
import com.umn.cacheserver.policy.EvictionPolicy;
import com.umn.cacheserver.policy.LFU;
import com.umn.cacheserver.policy.LRU;
import com.umn.cacheserver.policy.LearnedEviction;
import com.umn.cacheserver.repository.CacheDataRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class CacheService {
    private final CacheDataRepository cacheRepo;
    private final String policy;
    private EvictionPolicy evictionPolicy;
    private RestTemplate restTemplate;
    private String flaskURL;

    @Autowired
    CacheService(CacheDataRepository cacheRepo, @Value("${cache.policy}") String policy, @Value("${flask.url}") String flaskURL, RestTemplate restTemplate) {
        this.cacheRepo = cacheRepo;
        this.policy = policy;
        this.restTemplate = restTemplate;
        this.flaskURL = flaskURL;

        switch (policy) {
            case "Learned":
                evictionPolicy = new LearnedEviction(restTemplate, flaskURL);
                break;
            case "LRU":
                evictionPolicy = new LRU();
                break;
            case "LFU":
                evictionPolicy = new LFU();
                break;
            default:
                System.out.println("Invalid Policy");
                System.exit(0);
        }
    }

    /**
     * Returns the value corresponding to the key
     */
    public synchronized Pair<String, Boolean> getValue(String key) {
        if (Cache.lookup.get(key) != null)
            return updateAndReturn(Cache.lookup.get(key));
        else return getPersistentValue(key);
    }

    /**
     * Puts the K,V pair to the cache.
     */
    public synchronized Pair<String, Boolean> postValue(String key, String value) {
        // If key is present in the the cache
        if (Cache.lookup.get(key) != null) {
            updateCache(Cache.cache.get(Cache.lookup.get(key)));
            Cache.cache.get(Cache.lookup.get(key)).setValue(value);
            return new Pair<>(key, true);
        } else {
            putNewEntry(key, value);
            return new Pair<>(key, false);
        }
    }

    /**
     * Calls evict, replaces the KV at the index returned by evict
     */
    private synchronized void putNewEntry(String key, String value) {
        int ind = -1;

        CacheEntry newEntry = new CacheEntry(key, value, new Timestamp(System.currentTimeMillis()));

        if (Cache.lookup.size() < Cache.cacheSize) {
            ind = Cache.lookup.size();
            Cache.cache.add(ind, newEntry);
        } else {
            try {
                ind = evictionPolicy.evict();
                cacheRepo.save(new CacheData(Cache.cache.get(ind).getKey(), Cache.cache.get(ind).getValue()));
                Cache.lookup.remove(Cache.cache.get(ind).getKey());
                Cache.cache.set(ind, newEntry);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Cache.lookup.put(key, ind);
    }

    /**
     * Update the index and returns the value
     */
    private Pair<String, Boolean> updateAndReturn(int index) {
        updateCache(Cache.cache.get(index));
        return new Pair<>(Cache.cache.get(index).getValue(), true);
    }

    /**
     * Update the cache entry with the latest timestamp and increments the count
     */
    private void updateCache(CacheEntry entry) {
        entry.setTimestamp(new Timestamp(System.currentTimeMillis()));
        entry.incFrequency();
    }

    /**
     * Send the info to the learning module and returns the value of the entry
     */
    public Pair<String, Boolean> getPersistentValue(String key) {
        CacheData value = cacheRepo.findByKey(key);

        if (value != null) {
            putNewEntry(key, value.getValue());
            return new Pair<>(value.getValue(), false);
        }
        return null;
    }
}
