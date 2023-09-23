package com.nitish.redis.dao;

import com.nitish.redis.queue.RedisInsertValues;

import java.util.List;

public interface ValueOperationDao<T> extends RedisDao<T> {

    void add(String key, Long value);

    String getRecord(String key);

    void deleteRecord(String key);

    Long increment(String key);

    void storeBulkData(List<RedisInsertValues<T>> redisInsertValues);

}
