package com.nitish.redis.dao;

import com.nitish.redis.queue.RedisInsertValues;

import java.util.List;
import java.util.Map;

public interface HashOperationDao<T> extends RedisDao<T> {

    void add(String key,T entity);

    long getNumberOfRecords(T entity);

    Map<String,T> getAllRecords(T entity);

    T getRecord(String key, T entity);

    void deleteRecord(String key, T entity);

    void deleteAllRecord(T entity);

    List<T> getRecords(T entity);

    void storeBulkData(List<RedisInsertValues<T>> redisInsertValues);
}
