package com.nitish.redis.queue;

import org.springframework.data.redis.core.ListOperations;

import java.util.List;

public interface RedisDataInsertRepository<T> {

    void save(ListOperations<String, T> operations, List<RedisInsertValues<T>> redisInsertValues);

}
