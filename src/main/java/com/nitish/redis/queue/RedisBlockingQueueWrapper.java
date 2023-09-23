package com.nitish.redis.queue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Data
public class RedisBlockingQueueWrapper<E> {
    private final CopyOnWriteArrayList<RedisBlockingQueue<E>> queues = new CopyOnWriteArrayList<>();
    private final RedisBlockingQueueParams params;
    private final Function<E, Integer> hashCodeFunction;
    private final int maxThreads;

    /**
     * Starts SqlBlockingQueues.
     *
     * @param  saveFunction function to save entities in database
     * @param  batchUpdateComparator comparator to sort entities by primary key to avoid deadlocks in cluster mode
     *                               NOTE: you must use all of primary key parts in your comparator
     */
    public void init(Consumer<List<E>> saveFunction, Comparator<E> batchUpdateComparator) {
        for (int i = 0; i < maxThreads; i++) {
            RedisBlockingQueue<E> queue = new RedisBlockingQueue<>(params);
            queues.add(queue);
            queue.init(saveFunction, batchUpdateComparator, i);
        }
    }

    public void add(E element) {
        int queueIndex = element != null ? (hashCodeFunction.apply(element) & 0x7FFFFFFF) % maxThreads : 0;
        queues.get(queueIndex).add(element);
    }

    public void destroy() {
        queues.forEach(RedisBlockingQueue::destroy);
    }
}
