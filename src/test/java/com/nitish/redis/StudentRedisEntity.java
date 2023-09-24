package com.nitish.redis;

import com.nitish.redis.model.RedisEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class StudentRedisEntity implements RedisEntity, Serializable {

    private String uuid;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;

}
