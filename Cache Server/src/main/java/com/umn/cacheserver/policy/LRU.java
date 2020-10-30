package com.umn.cacheserver.policy;

import com.umn.cacheserver.model.Cache;
import com.umn.cacheserver.model.CacheEntry;

import java.sql.Timestamp;

public class LRU {

    /**
     * Returns the index of the entry that needs to be replaced.
     */
    public int evictLRU(){
        Timestamp min = Cache.cache.get(0).getTimestamp();
        int ind = 0;
        for(CacheEntry entry:Cache.cache){
            if(entry.getTimestamp().compareTo(min) < 0){
                min = entry.getTimestamp();
                ind = Cache.lookup.get(entry.getKey());
            }
        }
        return ind;
    }
}
