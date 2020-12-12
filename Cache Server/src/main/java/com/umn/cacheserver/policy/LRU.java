package com.umn.cacheserver.policy;

import com.umn.cacheserver.model.Cache;
import com.umn.cacheserver.model.CacheEntry;

import java.sql.Timestamp;

public class LRU extends EvictionPolicy {

    /**
     * Returns the index of the entry that needs to be replaced.
     */
    @Override
    public int evict() {
        Timestamp min = Cache.cache.get(0).getTimestamp();
        int ind = 0;
        for (CacheEntry entry : Cache.cache) {
            if (entry.getTimestamp().before(min)) {
                min = entry.getTimestamp();
                ind = Cache.lookup.get(entry.getKey());
            }
        }
        System.out.println("Returning: " + ind);
        return ind;
    }
}
