package hello.models;

import java.util.concurrent.CompletableFuture;

public interface Provider {
    CompletableFuture<Void> setAsync(String key, String value);
    CompletableFuture<Void> setAndNotifyAsync(String key, String value);
    CompletableFuture<String> getAsync(String key);
    CompletableFuture<String> watchAsync(String key); 

    // void setAsync(String key, String value);
    // void setAndNotifyAsync(String key, String value);
    // String getAsync(String key);
    // String watchAsync(String key); 
}
