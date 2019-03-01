package hello.models;

import java.util.concurrent.CompletableFuture;

public interface Provider {
    // CompletableFuture<Void> setAsync(K key, V value);
    // CompletableFuture<Void> setAndNotifyAsync(K key, V value);
    // CompletableFuture<V> getAsync(K key);
    // V watchAsync(K key); 

    void setAsync(String key, String value);
    void setAndNotifyAsync(String key, String value);
    String getAsync(String key);
    void watchAsync(String key); 
}
