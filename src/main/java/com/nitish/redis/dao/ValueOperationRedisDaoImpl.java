package com.nitish.redis.dao;

import com.nitish.redis.model.RedisEntity;
import com.nitish.redis.queue.RedisInsertValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

public abstract class ValueOperationRedisDaoImpl<T extends RedisEntity> implements ValueOperationDao<T> {

    public abstract ValueOperations<String, String> getValueOperation();

    private static final Logger log = LogManager.getLogger(ValueOperationRedisDaoImpl.class);

    @Override
    public void add(String key, Long value) {
        getValueOperation().increment(key, value);
    }

    @Override
    public String getRecord(String key) {
        return getValueOperation().get(key);
    }

    @Override
    public Long increment(String key) {
        return getValueOperation().increment(key);
    }

    @Override
    public void deleteRecord(String key) {
        getValueOperation().getAndDelete(key);
    }


    @Override
    public void storeBulkData(List<RedisInsertValues<T>> redisInsertValues) {
        log.error("Start storeBulkData() : size : " + redisInsertValues.size());
        try {

            List<Object> results = getValueOperation().getOperations().executePipelined(
                    new SessionCallback<Object>() {

                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                            try {
                                for (int i = 0; i < redisInsertValues.size(); i++) {
                                    operations.opsForHash().put((K) redisInsertValues.get(i).getHeaderKey(), redisInsertValues.get(i).getKey(), (V) redisInsertValues.get(i).getEntity());
                                }
                            } catch (Exception e) {
                                log.error("Redis bulk execute issue " + e.getMessage());
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }
            );

            log.error("Demos " + results);
        } catch (Exception e) {
            log.error("Redis bulk issue : " + e.getMessage());
            e.printStackTrace();
        }

    }
}
