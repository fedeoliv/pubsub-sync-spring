package hello.models.executors;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisExecutor {
    private final StringRedisTemplate redisTemplate;
    
    public RedisExecutor(JedisConnectionFactory connectionFactory) {
        redisTemplate = createRedisTemplate(connectionFactory);
    }

    @Bean
    private StringRedisTemplate createRedisTemplate(JedisConnectionFactory connectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public void stringSet(String key, String value) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            stringConn.set(key, value);
            return null;
        });
    }

    public String stringGet(String key) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            return stringConn.get(key);
        });
    }

    public void publish(String key, String value) {
        redisTemplate.convertAndSend(key, value);
    }
}
