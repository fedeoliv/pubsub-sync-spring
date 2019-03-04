package hello.models.subscribers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {
    private static final int CAPACITY = 1;
    public final BlockingQueue<String> statusQueue = new ArrayBlockingQueue<String>(CAPACITY);

    public synchronized void onMessage(Message message, byte[] pattern) {
        try {
            statusQueue.put(message.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
