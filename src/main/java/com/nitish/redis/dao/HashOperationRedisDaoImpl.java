package com.nitish.redis.dao;

import com.nitish.redis.model.RedisEntity;
import com.nitish.redis.queue.RedisInsertValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class HashOperationRedisDaoImpl<T extends RedisEntity> implements HashOperationDao<T> {

    public abstract HashOperations<String,String,T> getHashOperation();

    private static final Logger log = LogManager.getLogger(HashOperationRedisDaoImpl.class);

    @Override
    public void add(String key, T entity) {
        getHashOperation().put(getRedisKey(entity),key, entity);
    }

    @Override
    public long getNumberOfRecords(T entity) {
        Long size = getHashOperation().size(getRedisKey(entity));
        return size==null ? 0 : size;
    }

    @Override
    public Map<String,T> getAllRecords(T entity) {
        return getHashOperation().entries(getRedisKey(entity));
    }

    @Override
    public T getRecord(String key, T entity) {
        return getHashOperation().get(getRedisKey(entity),key);
    }

    @Override
    public void deleteRecord(String key, T entity) {
        getHashOperation().delete(getRedisKey(entity),key);
    }

    @Override
    public void deleteAllRecord(T entity) {
        getHashOperation().getOperations().delete(getRedisKey(entity));
    }



    @Override
    public List<T> getRecords(T entity) {
        return new ArrayList<>(getHashOperation().values(getRedisKey(entity)));
    }

    @Override
    public void storeBulkData(List<RedisInsertValues<T>> redisInsertValues) {
        log.error("Start storeBulkData() : size : "+redisInsertValues.size());
        try {

            List<Object> results = getHashOperation().getOperations().executePipelined(
                    new SessionCallback<Object>() {

                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                            try {
                                for (int i = 0; i < redisInsertValues.size(); i++) {
                                    operations.opsForHash().put((K) redisInsertValues.get(i).getHeaderKey(), redisInsertValues.get(i).getKey(), (V) redisInsertValues.get(i).getEntity());
                                }
                            } catch (Exception e) {
                                log.error("Redis bulk execute issue "+e.getMessage());
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }
            );

            log.error("Demos " + results);
        }catch (Exception e){
            log.error("Redis bulk issue : "+e.getMessage());
            e.printStackTrace();
        }

    }
}
