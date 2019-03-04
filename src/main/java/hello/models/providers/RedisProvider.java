package hello.models.providers;

import java.util.Optional;
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
import hello.models.subscribers.RedisMessageSubscriber;
import static com.ea.async.Async.await;

public class RedisProvider implements Provider {
    private final JedisConnectionFactory connectionFactory;
    private final StringRedisTemplate redisTemplate;

    public RedisProvider(String hostName, int port) {
        connectionFactory = createConnectionFactory(hostName, port);
        redisTemplate = createRedisTemplate();
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        return CompletableFuture.runAsync(() -> {
            await(stringSet(key, value));
        });
    }

    @Override
    public CompletableFuture<Void> setAndNotifyAsync(String key, String value) {
        return CompletableFuture.runAsync(() -> {
            await(setAsync(key, value));
            await(publish(key, value));
        });
    }

    @Override
    public CompletableFuture<String> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            return await(stringGet(key.toString()));
        });
    }

    @Override
    public synchronized CompletableFuture<Optional<String>> watchAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            return await(subscribeAsync(key));
        });
    }

    @Bean
    private StringRedisTemplate createRedisTemplate() {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private JedisConnectionFactory createConnectionFactory(String hostName, int port) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(hostName, port);
        // redisConfig.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        return new JedisConnectionFactory(redisConfig);
    }

    private CompletableFuture<Void> stringSet(String key, String value) {
        return CompletableFuture.runAsync(() -> {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                StringRedisConnection stringConn = (StringRedisConnection) connection;
                stringConn.set(key, value);
                return null;
            });
        });
    }

    private CompletableFuture<String> stringGet(String key) {
        return CompletableFuture.supplyAsync(() -> {
            return redisTemplate.execute((RedisCallback<String>) connection -> {
                StringRedisConnection stringConn = (StringRedisConnection) connection;
                return stringConn.get(key);
            });
        });
    }

    private CompletableFuture<Void> publish(String key, String value) {
        return CompletableFuture.runAsync(() -> {
            redisTemplate.convertAndSend(key, value);
        });
    }

    private CompletableFuture<Optional<String>> subscribeAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            RedisMessageSubscriber subscriber = subscribe(key);
            return getStatus(subscriber);
        });
    }

    private RedisMessageSubscriber subscribe(String key) {
        RedisMessageSubscriber subscriber = new RedisMessageSubscriber();
        RedisMessageListenerContainer container = createContainer(subscriber, key);

        container.start();

        return subscriber;
    }

    private RedisMessageListenerContainer createContainer(RedisMessageSubscriber subscriber, String key) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber);
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(adapter, new ChannelTopic(key));
        container.afterPropertiesSet();

        return container;
    }

    private Optional<String> getStatus(RedisMessageSubscriber subscriber) {
        try {
            return Optional.of(subscriber.internalQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
