package com.umn.cacheserver.policy;

import com.umn.cacheserver.model.Cache;
import com.umn.cacheserver.model.CacheEntry;
import com.umn.cacheserver.model.DatasetEntry;

import java.sql.Timestamp;
import java.util.*;

public class LearnedEviction extends EvictionPolicy{

    /**
     *  Returns the index
     */
    @Override
    public int evict() {
        ArrayList<String> cacheValues = new ArrayList<>();
        ArrayList<Integer> freqList = new ArrayList<>();
        Timestamp timeArray[] = new Timestamp[Cache.cacheSize];
        int index=0;
        for(CacheEntry entry : Cache.cache){
            timeArray[index] = entry.getTimestamp();
            freqList.add(entry.getFrequency());
            cacheValues.add(entry.getValue());
            index++;
        }
        DatasetEntry cacheSnapshot = new DatasetEntry(cacheValues, freqList, rankArr(timeArray));

        return 0;
    }


    ArrayList<Integer> rankArr(Timestamp[] input) {
        // Copy input array into newArray
        Timestamp[] newArray = Arrays.copyOfRange(input, 0, input.length);

        // Sort newArray[] in ascending order
        Arrays.sort(newArray);
        int i;

        // Map to store the rank of the array element
        Map<Timestamp, Integer> ranks = new HashMap<>();

        int rank = 1;

        for (Timestamp element : newArray) {

            // Update rank of element
            if (ranks.get(element) == null) {
                ranks.put(element, rank);
                rank++;
            }
        }

        // Assign ranks to elements
        ArrayList<Integer> rankedInput  = new ArrayList<>();
        for (Timestamp element : input) {
            rankedInput.add(ranks.get(element));
        }

        return rankedInput;
    }
}
