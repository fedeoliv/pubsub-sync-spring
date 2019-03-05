package hello.utils;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class CompletableFutureHelper {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
	 * Ensures the CompletableFuture finish within the given time.
	 *
	 * @param future a completable future
     * @param duration a timeout duration for the given completable future
     * @throws TimeoutException if the CompletableFuture takes too long to complete
     * @return a completable future with timeout handling
	 */
    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity());
    }
 
    private static <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), TimeUnit.MILLISECONDS);

        return promise;
    }
}
