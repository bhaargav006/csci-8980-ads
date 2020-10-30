package com.umn.cacheserver.model;

import org.springframework.data.annotation.Id;


public class CacheData {

    @Id
    public String key;

    public String value;

    public CacheData() {
    }

    public CacheData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[key=%s, value='%s']",
                key, value);
    }

}