package com.nitish.redis.dao;

import com.nitish.redis.model.RedisEntity;
import com.nitish.redis.queue.RedisInsertValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

public abstract class ListOperationRedisDaoImpl<T extends RedisEntity> implements ListOperationDao<T> {

    public abstract ListOperations<String, T> getListOperation();

    private static final Logger log = LogManager.getLogger(ListOperationRedisDaoImpl.class);

    /**
     *
     * @param entity
     * @return Integer reply: the length of the list after the push operation.
     */
    @Override
    public Long add(T entity) {
        return getListOperation().rightPush(getRedisKey(entity), entity);
    }

    @Override
    public long getNumberOfRecords(T entity) {
        Long size = getListOperation().size(getRedisKey(entity));
        return size==null ? 0 : size;
    }

    @Override
    public List<T> getAllRecords(T entity) {
        long size = getNumberOfRecords(entity);
        return getListOperation().range(getRedisKey(entity).trim(), 0, size);
    }

    @Override
    public void remove(T entity) {
        getListOperation().getOperations().delete(getRedisKey(entity).trim());
    }

    @Override
    public void storeBulkData(List<RedisInsertValues<T>> redisInsertValues) {
        log.error("Start storeBulkData() : size : "+redisInsertValues.size());
        try {

            List<Object> results = getListOperation().getOperations().executePipelined(
                    new SessionCallback<Object>() {

                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                            try {
                                for (int i = 0; i < redisInsertValues.size(); i++) {
                                    operations.opsForList().leftPush((K) redisInsertValues.get(i).getKey(), (V) redisInsertValues.get(i).getEntity());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }
            );

            log.error("List Operation Redis Result " + results);
        }catch (Exception e){
            log.error("Redis bulk issue : "+e.getMessage());
            e.printStackTrace();
        }

    }
}
