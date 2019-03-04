package hello;

import org.springframework.web.bind.annotation.RestController;
import hello.models.RedisProvider;
import hello.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static com.ea.async.Async.await;

@RestController
public class HelloController {
    // @Autowired
    RedisProvider provider;

    public HelloController() {
        provider = new RedisProvider("localhost", 6379);
    }

    @RequestMapping("/")
    public String index() {
        return "HELLO WORLD!";
    }

    @GetMapping("/channel")
    public ResponseEntity<String> start(@RequestParam String key) {
        if (StringUtils.isNullOrWhitespace(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        await(provider.setAsync(key, "Waiting"));
        
        String status = await(provider.watchAsync(key));

        return new ResponseEntity<String>(status, HttpStatus.OK);

        // try {
        //     provider.setAsync(key, "Waiting").get();
        //     String status = provider.watchAsync(key).get();

        //     return new ResponseEntity<String>(status, HttpStatus.OK);
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
        
        await(provider.setAndNotifyAsync(key, value));
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
