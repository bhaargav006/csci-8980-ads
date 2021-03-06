package com.umn.cacheserver.policy;

import com.umn.cacheserver.model.Cache;
import com.umn.cacheserver.model.CacheEntry;

public class LFU extends EvictionPolicy {

    /**
     * Returns the index of the entry that needs to be replaced.
     */
    @Override
    public int evict(){
        long min = Cache.cache.get(0).getFrequency();
        int ind = 0;
        for(CacheEntry entry:Cache.cache){
            if(entry.getFrequency() < min){
                min = entry.getFrequency();
                ind = Cache.lookup.get(entry.getKey());
            }
        }
        return ind;
    }
}
