package com.umn.cacheserver.policy;

public abstract class EvictionPolicy {
    /**
     * Returns the index of the  entry that needs to be replaced.
     */
    public abstract int evict();
}
