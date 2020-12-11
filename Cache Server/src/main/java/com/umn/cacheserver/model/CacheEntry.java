package com.umn.cacheserver.model;

import java.sql.Timestamp;

public class CacheEntry {
    String key;
    String value;
    int frequency;
    Timestamp ts;

    public CacheEntry(){
    }

    public CacheEntry(String key, String value, Timestamp ts){
        this.value = value;
        this.frequency = 1;
        this.ts = ts;
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public int getFrequency() {
        return frequency;
    }

    public Timestamp getTimestamp() {
        return ts;
    }

    synchronized public void setTimestamp(Timestamp ts) {
        if(this.ts.compareTo(ts) < 0)
            this.ts = ts;
    }

    synchronized public void setValue(String value) {
        this.value = value;
    }

    synchronized public void incFrequency(){
        frequency += 1;
    }
}
