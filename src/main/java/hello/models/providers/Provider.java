package hello.models.providers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Provider {
    CompletableFuture<Void> setAsync(String key, String value);
    CompletableFuture<Void> setAndNotifyAsync(String key, String value);
    CompletableFuture<Optional<String>> getAsync(String key);
    CompletableFuture<Optional<String>> watchAsync(String key);
}
