package com.realestate.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 캐시 Service
 *
 * @author Generated from toy-real-estate-backend
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    /**
     * Redis에 데이터 저장 (TTL 지정)
     *
     * @param key Redis 키
     * @param value 저장할 값
     * @param hours TTL (시간)
     */
    public void setValues(String key, Object value, long hours) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, value, Duration.ofHours(hours));
            log.debug("Redis set - key: {}, TTL: {} hours", key, hours);
        } catch (Exception e) {
            log.error("Failed to set value in Redis - key: {}", key, e);
        }
    }
    
    /**
     * Redis에 데이터 저장 (만료시간 없음)
     *
     * @param key Redis 키
     * @param value 저장할 값
     */
    public void setValues(String key, Object value) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, value);
            log.debug("Redis set - key: {}", key);
        } catch (Exception e) {
            log.error("Failed to set value in Redis - key: {}", key, e);
        }
    }
    
    /**
     * Redis에서 데이터 조회
     *
     * @param key Redis 키
     * @return 저장된 값
     */
    @SuppressWarnings("unchecked")
    public <T> T getValues(String key, Class<T> type) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            Object value = values.get(key);
            
            if (value == null) {
                return null;
            }
            
            // List 타입 처리
            if (List.class.isAssignableFrom(type)) {
                return (T) value;
            }
            
            // 일반 객체 타입 처리
            return objectMapper.convertValue(value, type);
            
        } catch (Exception e) {
            log.error("Failed to get value from Redis - key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Redis 키 삭제
     *
     * @param key Redis 키
     */
    public void deleteValues(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Redis delete - key: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete value from Redis - key: {}", key, e);
        }
    }
    
    /**
     * Redis 키 존재 여부 확인
     *
     * @param key Redis 키
     * @return 존재 여부
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check key existence - key: {}", key, e);
            return false;
        }
    }
    
    /**
     * Redis 키 만료시간 설정
     *
     * @param key Redis 키
     * @param timeout 만료시간
     * @param unit 시간 단위
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
            log.debug("Redis set expire - key: {}, timeout: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Failed to set expire - key: {}", key, e);
        }
    }
    
    /**
     * 패턴에 맞는 모든 키 삭제
     *
     * @param pattern 키 패턴
     */
    public void deleteByPattern(String pattern) {
        try {
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Redis delete by pattern - pattern: {}, deleted: {} keys", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("Failed to delete by pattern - pattern: {}", pattern, e);
        }
    }
}
