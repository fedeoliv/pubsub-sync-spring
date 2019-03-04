package hello.models.providers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import hello.models.executors.RedisExecutor;
import hello.models.subscribers.RedisMessageSubscriber;
import static com.ea.async.Async.await;

public class RedisProvider implements Provider {
    private final JedisConnectionFactory connectionFactory;
    private final RedisExecutor executor;

    public RedisProvider(String hostName, int port) {
        connectionFactory = createConnectionFactory(hostName, port);
        executor = new RedisExecutor(connectionFactory);
    }

    @Override
    public CompletableFuture<Void> setAsync(String key, String value) {
        return CompletableFuture.runAsync(() -> {
            executor.stringSet(key, value);
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

    private JedisConnectionFactory createConnectionFactory(String hostName, int port) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(hostName, port);
        // redisConfig.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        return new JedisConnectionFactory(redisConfig);
    }

    private CompletableFuture<String> stringGet(String key) {
        return CompletableFuture.supplyAsync(() -> {
            return executor.stringGet(key);
        });
    }

    private CompletableFuture<Void> publish(String key, String value) {
        return CompletableFuture.runAsync(() -> {
            executor.publish(key, value);
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
