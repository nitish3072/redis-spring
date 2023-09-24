package com.nitish.redis;


import com.nitish.redis.dao.HashOperationRedisDaoImpl;
import com.nitish.redis.queue.RedisInsertValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class StudentDataRedisListOperationDaoImpl extends HashOperationRedisDaoImpl<StudentRedisEntity> implements StudentDataRedisListOperationDao<StudentRedisEntity> {

    @Autowired
    protected RedisTemplate<String, StudentRedisEntity> redisTemplate;

    @Override
    public HashOperations<String, String, StudentRedisEntity> getHashOperation() {
        return redisTemplate.opsForHash();
    }

    @Override
    public String getRedisKey(StudentRedisEntity entry) {
        return this.getClass().getName() + ":" + "map" + ":" + entry.getUuid();
    }

    @Override
    public void addToHash(StudentRedisEntity entity) {
        RedisInsertValues<StudentRedisEntity> values = new RedisInsertValues<>();
        values.setKey(getRedisKey(entity));
        values.setEntity(entity);
        add(getRedisKey(entity), entity);
    }

    @Override
    public StudentRedisEntity getData(String uuid) {
        StudentRedisEntity studentRedisEntity = new StudentRedisEntity();
        studentRedisEntity.setUuid(uuid);
        return getRecord(uuid, studentRedisEntity);
    }

    @Override
    public void deleteData(String uuid) {
        StudentRedisEntity studentRedisEntity = new StudentRedisEntity();
        studentRedisEntity.setUuid(uuid);
        deleteRecord(uuid, studentRedisEntity);
    }

}