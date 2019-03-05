package hello.models.providers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import hello.models.Transaction;

public interface Provider {
    CompletableFuture<Void> setAsync(Transaction transaction);
    CompletableFuture<Void> setAndNotifyAsync(Transaction transaction);
    CompletableFuture<Optional<String>> getAsync(String key);
    CompletableFuture<Optional<String>> watchAsync(Transaction transaction);
}
