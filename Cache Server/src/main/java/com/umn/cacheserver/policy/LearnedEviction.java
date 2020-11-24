package com.umn.cacheserver.policy;

import java.util.List;

public class LearnedEviction extends EvictionPolicy{

    /**
     *  Returns the index
     */
    @Override
    public int evict() {
        return 0;
    }

    List<String> sortByFrequency(){
        return null;
    }

    List<String> sortByRecency(){
        return null;
    }
}
