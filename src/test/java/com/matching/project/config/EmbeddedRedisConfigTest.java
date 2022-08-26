package com.matching.project.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.matching.project.dto.user.UserDto;
import com.matching.project.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EmbeddedRedisConfig.class)
@AutoConfigureMockMvc
@Transactional
public class EmbeddedRedisConfigTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisService redisService;

    @Test
    void redisConnectionTest() {
        //given
        final String key = "10295710928315";
        final String value = "connectionTest";

        //when
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);

        //then
        final String s = (String)valueOperations.get(key);
        assertThat(s).isEqualTo(value);
    }

    @Test
    void redisExpireTest() throws InterruptedException {
        //given
        final String key = "10295710928315";
        final String value = "expireTest";

        //when
        final ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
        final Boolean expire = redisTemplate.expire(key, 1, TimeUnit.SECONDS);
        Thread.sleep(1500);

        //then
        final String s = (String)valueOperations.get(key);
        assertThat(expire).isTrue();
        assertThat(s).isNull();
    }

    @Test
    void redisInsertObjectTest() throws JsonProcessingException {
        //given
        String key = "10295710928315";
        UserDto objectValue = new UserDto(1L, "insertObjectTest", true);
        redisService.set(key, objectValue);

        //when
        UserDto result = redisService.get(key, UserDto.class);

        //then
        assertThat(result.getNo()).isEqualTo(objectValue.getNo());
        assertThat(result.getName()).isEqualTo(objectValue.getName());
        assertThat(result.isRegister()).isEqualTo(objectValue.isRegister());

    }
}
