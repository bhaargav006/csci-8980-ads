package com.umn.cacheserver.repository;

import java.util.List;

import com.umn.cacheserver.model.CacheData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<CacheData, String> {

  public CacheData findByKey(String key);

}