package hello.models;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

// @Service
public class RedisProvider implements Provider {
    private final StringRedisTemplate redisTemplate;

    public RedisProvider(String hostName, int port) {
        redisTemplate = redisTemplate(hostName, port);
    }

    @Override
    public void setAsync(String key, String value) {
        // RedisMessagePublisher publisher = createPublisher(key.toString());

        stringSet(key, value);
    }

    @Override
    public void setAndNotifyAsync(String key, String value) {
        setAsync(key, value);
        publish(key, value);
    }

    @Override
    public String getAsync(String key) {
        return stringGet(key.toString());
    }

    @Override
    public void watchAsync(String key) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer(); 

        container.setConnectionFactory(jedisConnectionFactory("localhost", 6379)); 
        container.addMessageListener(messageListener(), new ChannelTopic(key)); 
        container.afterPropertiesSet();
        container.start();
    }

    MessageListenerAdapter messageListener() { 
        return new MessageListenerAdapter(new RedisMessageSubscriber());
    }

    @Bean
    private StringRedisTemplate redisTemplate(String hostName, int port) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(hostName, port));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private JedisConnectionFactory jedisConnectionFactory(String hostName, int port) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
        // redisStandaloneConfiguration.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    private void stringSet(String key, String value) {

        redisTemplate.execute((RedisCallback<String>) connection -> {
			StringRedisConnection stringConn = (StringRedisConnection) connection;
			stringConn.set(key, value);
			return null;
        });
    }

    private String stringGet(String key) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
			StringRedisConnection stringConn = (StringRedisConnection) connection;
			return stringConn.get(key);
        });
    }

    private void publish(String key, String value) {
        redisTemplate.convertAndSend(key, value);
    }
}
