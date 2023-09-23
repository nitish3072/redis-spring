package com.nitish.redis.dao;

import com.nitish.redis.model.RedisEntity;
import com.nitish.redis.queue.RedisInsertValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

public abstract class ZSetOperationRedisDaoImpl<T extends RedisEntity> implements ZSetOperationDao<T> {

    public abstract ZSetOperations<String, T> getZSetOperation();

    private static final Logger log = LogManager.getLogger(ZSetOperationRedisDaoImpl.class);

    /**
     * @param entity
     * @return Integer reply: the length of the list after the push operation.
     */
    @Override
    public Boolean add(T entity, double score) {
        return getZSetOperation().add(getRedisKey(entity), entity, score);
    }

    @Override
    public long getNumberOfRecords(T entity) {
        Long size = getZSetOperation().size(getRedisKey(entity));
        return size == null ? 0 : size;
    }

    @Override
    public Set<T> getAllRecords(T entity) {
        long size = getNumberOfRecords(entity);
        return getZSetOperation().range(getRedisKey(entity).trim(), 0, size);
    }

    @Override
    public Set<T> getAllRecords(String key, double startScore, double endScore) {
        return getZSetOperation().rangeByScore(key, startScore, endScore);
    }


    @Override
    public Set<ZSetOperations.TypedTuple<T>> getAllRecordsByTimeStamp(String key, Long startScore, Long endScore) {
        return getZSetOperation().rangeWithScores(key, startScore, endScore);
    }

    @Override
    public void remove(T entity) {
        getZSetOperation().getOperations().delete(getRedisKey(entity).trim());
    }

    @Override
    public void remove(String key, long startScore, long endScore) {
        (getZSetOperation()).removeRange(key, startScore, endScore);
    }

    @Override
    public void storeBulkData(List<RedisInsertValues<T>> redisInsertValues) {
        log.error("Start storeBulkData() : size : " + redisInsertValues.size());
        try {

            List<Object> results = getZSetOperation().getOperations().executePipelined(
                    new SessionCallback<Object>() {

                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                            try {
                                for (int i = 0; i < redisInsertValues.size(); i++) {
                                    operations.opsForZSet().add((K) redisInsertValues.get(i).getKey(), (V) redisInsertValues.get(i).getEntity(), redisInsertValues.get(i).getScore());
                                }
                            } catch (Exception e) {
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
