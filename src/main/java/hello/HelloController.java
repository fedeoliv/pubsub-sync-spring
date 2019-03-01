package hello;

import org.springframework.web.bind.annotation.RestController;

import hello.models.Provider;
import hello.models.RedisProvider;
import hello.utils.StringUtils;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HelloController {
    // @Autowired
    // RedisProvider provider;

    @RequestMapping("/")
    public String index() {
        return "HELLO WORLD!";
    }

    @GetMapping("/channel")
    public ResponseEntity<String> start(@RequestParam String key) {
        if (StringUtils.isNullOrWhitespace(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        
        RedisProvider provider = new RedisProvider("localhost", 6379);
        provider.setAsync(key, "Waiting");

        String status = provider.watchAsync(key);
        
        return new ResponseEntity<String>(status, HttpStatus.OK);

        // try {
        //     provider.setAsync(key, "Waiting").get();
        //     String finalStatus = provider.watchAsync(key);
            
        //     return new ResponseEntity<String>(finalStatus, HttpStatus.OK);

        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // }

		// return new ResponseEntity<String>("Error", HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key, @RequestParam String value) {
        if (StringUtils.isNullOrWhitespace(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        if (StringUtils.isNullOrWhitespace(value)) {
            throw new IllegalArgumentException("Invalid value");
        }
        
        RedisProvider provider = new RedisProvider("localhost", 6379);
        provider.setAndNotifyAsync(key, value);
        
        return new ResponseEntity<String>(HttpStatus.OK);

        // try {
        //     provider.setAsync(key, "Waiting").get();
        //     String finalStatus = provider.watchAsync(key);
            
        //     return new ResponseEntity<String>(finalStatus, HttpStatus.OK);

        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // }

		// return new ResponseEntity<String>("Error", HttpStatus.NOT_ACCEPTABLE);
    }
}
