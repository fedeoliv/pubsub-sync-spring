package hello.models.executors;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import hello.models.Transaction;

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

    /**
	 * Set key to hold the string value. 
     * If key already holds a value, it is overwritten.
	 *
	 * @param channel the Redis channel
     * @param value a Redis value associated to a channel
	 */
    public void stringSet(Transaction transaction) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            stringConn.set(transaction.getId(), transaction.getStatus());
            return null;
        });
    }

    /**
	 * Get the value of channel. 
     * If the channel does not exist, an empty string is returned.
	 *
	 * @param channel the Redis channel
     * @return an optional string that represents a value associated to a key.
	 */
    public Optional<String> stringGet(String channel) {
        return redisTemplate.execute((RedisCallback<Optional<String>>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            return Optional.of(stringConn.get(channel));
        });
    }

    /**
     * Publishes the message to the given channel.
     *
     * @param channel the Redis channel
     * @param value a Redis value associated to a channel
     */
    public void publish(Transaction transaction) {
        redisTemplate.convertAndSend(transaction.getId(), transaction.getStatus());
    }
}
