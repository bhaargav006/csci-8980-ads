package com.umn.cacheserver.service;

import com.umn.cacheserver.model.Cache;
import com.umn.cacheserver.model.CacheData;
import com.umn.cacheserver.model.CacheEntry;
import com.umn.cacheserver.policy.EvictionPolicy;
import com.umn.cacheserver.policy.LFU;
import com.umn.cacheserver.policy.LRU;
import com.umn.cacheserver.repository.CacheDataRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class CacheService {
    private final CacheDataRepository cacheRepo;
    private final String policy;
    private EvictionPolicy evictionPolicy;


    @Autowired
    CacheService(CacheDataRepository cacheRepo, @Value("${cache.policy}") String policy) {
        this.cacheRepo = cacheRepo;
        this.policy = policy;
    }

    /**
     * Returns the value corresponding to the key
     */
    public synchronized Pair<String, Boolean> getValue(String key) {
        return Optional.ofNullable(Cache.lookup.get(key))
                .map(this::updateAndReturn)
                .orElse(getPersistentValue(key));
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

    private synchronized void putNewEntry(String key, String value) {
        int ind;

        CacheEntry newEntry = new CacheEntry(key, value, new Timestamp(System.currentTimeMillis()));

        if (Cache.lookup.size() < Cache.cacheSize) {
            ind = Cache.lookup.size();
        } else {
            String evictKey = evict();
            ind = Cache.lookup.get(evictKey);
            cacheRepo.save(new CacheData(evictKey, Cache.cache.get(ind).getValue()));
            Cache.lookup.remove(Cache.cache.get(ind).getKey());
        }

        Cache.cache.add(ind, newEntry);
        Cache.lookup.put(key, ind);
    }

    /**
     * Evicts the Cache based on chosen policy and return the index that was evicted.
     */
    private String evict() {
        switch (policy){
            case "LRU" :
                evictionPolicy = new LRU();
                break;
            case "LFU" :
                evictionPolicy = new LFU();
                break;
            default :
                System.out.println("Invalid Policy");
                System.exit(0);
        }
        int index = evictionPolicy.evict();

        return Cache.cache.get(index).getKey();
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
        String evictKey = evict();
        int ind = Cache.lookup.get(evictKey);

        CacheData value = cacheRepo.findByKey(key);
        //TODO replace the cache entry at ind

        return new Pair<>(value.getValue(), false);
    }
}
