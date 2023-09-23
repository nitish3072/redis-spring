package com.nitish.redis.dao;

import com.nitish.redis.queue.RedisInsertValues;

import java.util.List;

public interface ListOperationDao<T> extends RedisDao<T> {

    Long add(T entity);

    long getNumberOfRecords(T entity);

    List<T> getAllRecords(T entity);

    void remove(T entity);

    void storeBulkData(List<RedisInsertValues<T>> redisInsertValues);

}
