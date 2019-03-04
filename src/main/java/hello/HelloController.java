package hello;

import org.springframework.web.bind.annotation.RestController;
import hello.models.providers.Provider;
import hello.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;
import static com.ea.async.Async.await;

@RestController
public class HelloController {
    @Autowired
    Provider provider;

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
        
        Optional<String> status = await(provider.watchAsync(key));

        return status.isPresent() 
            ? new ResponseEntity<String>(status.get(), HttpStatus.OK)
            : new ResponseEntity<String>("Invalid status", HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key, @RequestParam String value) {
        if (StringUtils.isNullOrWhitespace(key) || StringUtils.isNullOrWhitespace(value)) {
            throw new IllegalArgumentException("Invalid key/value");
        }
        
        await(provider.setAndNotifyAsync(key, value));
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
