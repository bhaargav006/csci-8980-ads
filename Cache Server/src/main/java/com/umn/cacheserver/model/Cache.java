package com.umn.cacheserver.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

    int cacheSize;
    public static ConcurrentHashMap<String, Integer> lookup;
    public static List<CacheEntry> cache;
    volatile int cacheHit;

    public Cache(){
    }

    public Cache(int cacheSize){
        cache = Collections.synchronizedList(new ArrayList<>(cacheSize));
        cacheHit = 0;
    }

    /**
     * Returns the value corresponding to the key
     */
    public String getValue(String key){
        //TODO make it thread-safe
        return Optional.ofNullable(lookup.get(key))
                .map(this::updateAndReturn)
                .orElse(getPersistentValue(key));
    }

    /**
     * Puts the K,V pair to the cache.
     */
    public void postValue(String key, String value){
        //TODO make it thread-safe
        if(lookup.get(key) != null){
            CacheEntry entry = cache.get(lookup.get(key));
            updateCache(entry);
            entry.setValue(value);
        }
        else {
            int ind;

            CacheEntry new_entry = new CacheEntry(key, value, new Timestamp(System.currentTimeMillis()));

            //TODO critical section

            if(lookup.size() < cacheSize){
                ind = lookup.size();
            }
            else {
                ind = evict();
                lookup.remove(cache.get(ind).getKey());
                //TODO send entry at ind to Mongo
            }

            cache.add(ind, new_entry);
            lookup.put(key,ind);
        }
   }

    /**
     * Evicts the Cache based on chosen policy and return the index that was evicted.
     */
    private int evict() {
        return 0;
    }

    /**
     * Update the index and returns the value
     */
    private String updateAndReturn(int index) {
        cacheHit += 1;
        CacheEntry entry = cache.get(index);
        updateCache(entry);
        return entry.getValue();
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
    public String getPersistentValue(String key){
        int ind = evict();
        //TODO send entry at ind to Mongo
        //TODO Put Value from Mongo to ind
        //TODO update cache and lookup
        return null;
    }
}
