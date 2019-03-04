package hello.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {
    public final BlockingQueue<String> internalQueue = new ArrayBlockingQueue<String>(1);

    public synchronized void onMessage(Message message, byte[] pattern) {
        System.out.println("Message received: " + message.toString());

        try {
            internalQueue.put(message.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
