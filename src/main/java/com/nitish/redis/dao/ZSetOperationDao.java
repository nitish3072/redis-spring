package com.nitish.redis.dao;

import com.nitish.redis.queue.RedisInsertValues;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

public interface ZSetOperationDao<T> extends RedisDao<T> {

    Boolean add(T entity, double score);

    long getNumberOfRecords(T entity);

    Set<T> getAllRecords(T entity);

    Set<T> getAllRecords(String key, double startScore, double endScore);

    Set<ZSetOperations.TypedTuple<T>> getAllRecordsByTimeStamp(String key, Long startScore, Long endScore);

    void remove(T entity);

    void remove(String key, long startScore, long endScore);

    void storeBulkData(List<RedisInsertValues<T>> redisInsertValues);

}
