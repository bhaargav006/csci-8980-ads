package com.umn.cacheserver.model;

import java.util.ArrayList;

public class DatasetEntry {
    ArrayList<String> blockTrace;
    ArrayList<Integer> recency;
    ArrayList<Integer> frequency;

    public DatasetEntry(ArrayList<String> values, ArrayList<Integer> recency, ArrayList<Integer> frequency){
        this.blockTrace = values;
        this.recency = recency;
        this.frequency = frequency;
    }
}
