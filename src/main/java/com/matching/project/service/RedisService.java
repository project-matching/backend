package com.matching.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.project.error.CustomException;
import com.matching.project.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> classType){
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        T obj = null;
        if (ObjectUtils.isEmpty(redisValue)) {
            return null;
        } else {
            try {
                obj = objectMapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                throw new CustomException(ErrorCode.INVALID_JSON_PARSE);
            }
            return obj;
        }
    }

    public void set(String key, Object classType){
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(classType));
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INVALID_JSON_PARSE);
        }
    }

    public void set(String key, Object classType, int minutes){
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(classType), minutes, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INVALID_JSON_PARSE);
        }
    }

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
