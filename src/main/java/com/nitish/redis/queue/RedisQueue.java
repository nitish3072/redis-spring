package com.nitish.redis.queue;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public interface RedisQueue<E> {

    void init(Consumer<List<E>> saveFunction, Comparator<E> batchUpdateComparator, int queueIndex);

    void destroy();

    void add(E element);
}
