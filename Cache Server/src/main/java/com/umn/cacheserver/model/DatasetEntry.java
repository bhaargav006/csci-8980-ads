package com.umn.cacheserver.model;

import java.util.ArrayList;
import java.util.List;

public class DatasetEntry {
    List<Integer> blockTrace;
    List<Integer> recency;
    List<Integer> frequency;

    public DatasetEntry(List<Integer> values, List<Integer> recency, List<Integer> frequency) {
        this.blockTrace = values;
        this.recency = recency;
        this.frequency = frequency;
    }

    public List<Integer> getBlockTrace() {
        return blockTrace;
    }

    public void setBlockTrace(List<Integer> blockTrace) {
        this.blockTrace = blockTrace;
    }

    public List<Integer> getRecency() {
        return recency;
    }

    public void setRecency(List<Integer> recency) {
        this.recency = recency;
    }

    public List<Integer> getFrequency() {
        return frequency;
    }

    public void setFrequency(List<Integer> frequency) {
        this.frequency = frequency;
    }
}
