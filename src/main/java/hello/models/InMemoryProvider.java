package hello.models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

// @Service
// public class InMemoryProvider<K, V> implements Provider<K, V> {

//     private final Map<K, V> storeDictionary;
//     private final Map<K, CompletableFuture<V>> actionDictionary;

//     public InMemoryProvider() {
//         storeDictionary = new HashMap<K, V>();
//         actionDictionary = new HashMap<K, CompletableFuture<V>>();
//     }

//     @Override
//     public CompletableFuture<Void> setAsync(K key, V value) {
//         if (!isValidParams(key, value)) {
//             throw new IllegalArgumentException("Invalid key/value");
//         }

//         return CompletableFuture.runAsync(() -> {
//             System.out.println(key + "-> " + value);
//             storeDictionary.put(key, value);
//         });
//     }

//     @Override
//     public CompletableFuture<Void> setAndNotifyAsync(K key, V value) {
//         return CompletableFuture.runAsync(() -> {
//             try {
//                 setAsync(key, value).get();
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             } catch (ExecutionException e) {
//                 e.printStackTrace();
//             }

//             notify(key);
//         });
//     }

//     @Override
//     public CompletableFuture<V> getAsync(K key) {
//         if (key == null) {
//             throw new IllegalArgumentException("Invalid key");
//         }

//         return CompletableFuture.supplyAsync(() -> {
//             return storeDictionary.get(key);
//         });
//     }

//     @Override
//     public V watchAsync(K key) {
//         if (key == null) {
//             throw new IllegalArgumentException("Invalid key");
//         }

//         CompletableFuture<V> keyWatcher = CompletableFuture.supplyAsync(() -> {
//             return storeDictionary.get(key);
//         });

//         actionDictionary.put(key, keyWatcher);

//         try {
//             V response = keyWatcher.get(5, TimeUnit.SECONDS);

//             return response;
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         } catch (ExecutionException e) {
//             e.printStackTrace();
//         } catch (TimeoutException e) {
//             e.printStackTrace();
//         }

//         return null;


//         // CompletableFuture<V> keyWatcher = CompletableFuture.supplyAsync(() -> {
//         //     return storeDictionary.get(key);
//         // });
//     }

//     private boolean isValidParams(K key, V value) {
//         return key != null && value != null;
//     }

//     private void notify(K key) {
//         if (actionDictionary.containsKey(key)) {
//             CompletableFuture<V> action = actionDictionary.get(key);

//             try {
//                 action.get();
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             } catch (ExecutionException e) {
//                 e.printStackTrace();
//             }
//         }
//     }

// }
