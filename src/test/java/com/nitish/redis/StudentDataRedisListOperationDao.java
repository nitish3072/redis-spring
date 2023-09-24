package com.nitish.redis;

import java.util.List;

public interface StudentDataRedisListOperationDao<T> {

    void addToHash(T entity);

    T getData(String uuid);

    void deleteData(String uuid);

}
