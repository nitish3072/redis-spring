package com.nitish.redis.queue;

import lombok.Getter;

public final class RedisQueueElement<E> {

    @Getter
    private final E entity;

    public RedisQueueElement(E entity) {
        this.entity = entity;
    }
}


