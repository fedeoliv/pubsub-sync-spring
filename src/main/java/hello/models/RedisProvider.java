package hello.models;

import java.util.concurrent.CompletableFuture;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import static com.ea.async.Async.await;

// @Service
public class RedisProvider implements Provider {
    private final StringRedisTemplate redisTemplate;

    public RedisProvider(String hostName, int port) {
        redisTemplate = redisTemplate(hostName, port);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        return CompletableFuture.supplyAsync(() -> {
            await(stringSet(key, value));
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> setAndNotifyAsync(String key, String value) {
        return CompletableFuture.supplyAsync(() -> {
            await(setAsync(key, value));
            await(publish(key, value));

            return null;
        });
    }

    @Override
    public CompletableFuture<String> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            return stringGet(key.toString());
        });
    }

    @Override
    public synchronized CompletableFuture<String> watchAsync(String key) {
        String lastValue = await(subscribeAsync(key));

        return CompletableFuture.supplyAsync(() -> {
            return lastValue;
        });
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

    private CompletableFuture<Void> stringSet(String key, String value) {
        return CompletableFuture.supplyAsync(() -> {
            redisTemplate.execute((RedisCallback<String>) connection -> {
                StringRedisConnection stringConn = (StringRedisConnection) connection;
                stringConn.set(key, value);
                return null;
            });

            return null;
        });

    }

    private String stringGet(String key) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            return stringConn.get(key);
        });
    }

    private CompletableFuture<Void> publish(String key, String value) {
        return CompletableFuture.supplyAsync(() -> {
            redisTemplate.convertAndSend(key, value);
            return null;
        });
    }

    private CompletableFuture<String> subscribeAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            RedisMessageSubscriber subscriber = new RedisMessageSubscriber();
            MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber);
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();

            container.setConnectionFactory(jedisConnectionFactory("localhost", 6379));
            container.addMessageListener(adapter, new ChannelTopic(key));
            container.afterPropertiesSet();
            container.start();

            String msg = null;

            try {
                msg = subscriber.internalQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return msg;
        });
    }
}
