package com.nitish.redis.queue;

import lombok.Data;

@Data
public class RedisInsertValues<T> {

    private String headerKey;
    private String key;
    private double score;
    private T entity;

}
