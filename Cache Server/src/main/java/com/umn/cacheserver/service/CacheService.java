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
    public synchronized void postValue(String key, String value) {
        // If key is present in the the cache
        if (Cache.lookup.get(key) != null) {
            CacheEntry entry = Cache.cache.get(Cache.lookup.get(key));
            updateCache(entry);
            entry.setValue(value);
        } else {
            putNewEntry(key, value);
        }
    }

    /**
     * Calls evict, replaces the KV at the index returned by evict
     *
     * @param key
     * @param value
     */
    private synchronized void putNewEntry(String key, String value) {
        int ind = -1;

        CacheEntry newEntry = new CacheEntry(key, value, new Timestamp(System.currentTimeMillis()));

        if (Cache.lookup.size() < Cache.cacheSize) {
            ind = Cache.lookup.size();
        } else {
            try {
                ind = evict();
                cacheRepo.save(new CacheData(Cache.cache.get(ind).getKey(), Cache.cache.get(ind).getValue()));
                Cache.lookup.remove(Cache.cache.get(ind).getKey());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return;
            }
        }

        Cache.cache.add(ind, newEntry);
        Cache.lookup.put(key, ind);
    }

    /**
     * Evicts the Cache based on chosen policy and return the index that was evicted.
     */
    private int evict() {
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

        int p = evictionPolicy.evict();
        System.out.println("Evicting Index: " + p);
        return p;
    }

    /**
     * Update the index and returns the value
     */
    private Pair<String, Boolean> updateAndReturn(int index) {
        CacheEntry entry = Cache.cache.get(index);
        updateCache(entry);
        return new Pair<>(entry.getValue(), true);
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
