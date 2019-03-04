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

public class RedisProvider implements Provider {
    private final JedisConnectionFactory connectionFactory;
    private final RedisExecutor executor;

    public RedisProvider(String hostName, int port) {
        connectionFactory = createConnectionFactory(hostName, port);
        executor = new RedisExecutor(connectionFactory);
    }

    @Override
    public CompletableFuture<Void> setAsync(String channel, String status) {
        return CompletableFuture.runAsync(() -> {
            executor.stringSet(channel, status);
        });
    }

    @Override
    public CompletableFuture<Void> setAndNotifyAsync(String channel, String status) {
        return CompletableFuture.runAsync(() -> {
            executor.stringSet(channel, status);
            executor.publish(channel, status);
        });
    }

    @Override
    public CompletableFuture<String> getAsync(String channel) {
        return CompletableFuture.supplyAsync(() -> {
            return executor.stringGet(channel);
        });
    }

    @Override
    public CompletableFuture<Optional<String>> watchAsync(String channel) {
        return CompletableFuture.supplyAsync(() -> {
            RedisMessageSubscriber subscriber = subscribe(channel);
            return getStatus(subscriber);
        });
    }

    private JedisConnectionFactory createConnectionFactory(String hostName, int port) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(hostName, port);
        // redisConfig.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        return new JedisConnectionFactory(redisConfig);
    }

    private RedisMessageSubscriber subscribe(String channel) {
        RedisMessageSubscriber subscriber = new RedisMessageSubscriber();
        RedisMessageListenerContainer container = createContainer(subscriber, channel);

        container.start();

        return subscriber;
    }

    private RedisMessageListenerContainer createContainer(RedisMessageSubscriber subscriber, String channel) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber);
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(adapter, new ChannelTopic(channel));
        container.afterPropertiesSet();

        return container;
    }

    private Optional<String> getStatus(RedisMessageSubscriber subscriber) {
        try {
            return Optional.of(subscriber.statusQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
