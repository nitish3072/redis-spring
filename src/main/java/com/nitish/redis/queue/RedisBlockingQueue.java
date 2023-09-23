package com.nitish.redis.queue;

import com.nitish.redis.config.ThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class RedisBlockingQueue<E> implements RedisQueue<E> {

    private final BlockingQueue<RedisQueueElement<E>> queue = new LinkedBlockingQueue<>();
    private final RedisBlockingQueueParams params;

    private ExecutorService executor;

    public RedisBlockingQueue(RedisBlockingQueueParams params) {
        this.params = params;
    }

    @Override
    public void init(Consumer<List<E>> saveFunction, Comparator<E> batchUpdateComparator, int index) {
        executor = Executors.newSingleThreadExecutor(ThreadFactory.forName("redis-queue-" + index + "-"));
        executor.submit(() -> {
            int batchSize = params.getBatchSize();
            long maxDelay = params.getMaxDelay();
            List<RedisQueueElement<E>> entities = new ArrayList<>(batchSize);
            while (!Thread.interrupted()) {
                try {
                    long currentTs = System.currentTimeMillis();
                    RedisQueueElement<E> attr = queue.poll(maxDelay, TimeUnit.MILLISECONDS);
                    if (attr == null) {
                        continue;
                    } else {
                        entities.add(attr);
                    }
                    queue.drainTo(entities, batchSize - 1);
                    boolean fullPack = entities.size() == batchSize;
                    if (log.isDebugEnabled()) {
                        log.debug("Going to save {} entities", entities.size());
                        log.trace("Going to save entities: {}", entities);
                    }
                    Stream<E> entitiesStream = entities.stream().map(RedisQueueElement::getEntity);
                    saveFunction.accept(
                            (params.isBatchSortEnabled() ? entitiesStream.sorted(batchUpdateComparator) : entitiesStream)
                                    .collect(Collectors.toList())
                    );
                    if (!fullPack) {
                        long remainingDelay = maxDelay - (System.currentTimeMillis() - currentTs);
                        if (remainingDelay > 0) {
                            Thread.sleep(remainingDelay);
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        log.info("Queue polling was interrupted");
                        break;
                    } else {
                        log.error("Failed to save {} entities", entities.size(), e);
                    }
                } finally {
                    entities.clear();
                }
            }
        });
    }

    @Override
    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public void add(E element) {
        queue.add(new RedisQueueElement<>(element));
    }
}
