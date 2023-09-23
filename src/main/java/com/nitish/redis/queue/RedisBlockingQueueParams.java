package com.nitish.redis.queue;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class RedisBlockingQueueParams {

    private final int batchSize;
    private final long maxDelay;
    private final boolean batchSortEnabled;

}
