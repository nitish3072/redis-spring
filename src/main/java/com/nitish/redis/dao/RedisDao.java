package com.nitish.redis.dao;

public interface RedisDao<T> {

    String getRedisKey(T entry);


}
