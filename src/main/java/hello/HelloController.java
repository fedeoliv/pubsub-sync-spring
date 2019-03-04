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
        return "Spring API with Observer pattern";
    }

    @GetMapping("/channel")
    public ResponseEntity<String> start(@RequestParam String channel) {
        if (StringUtils.isNullOrWhitespace(channel)) {
            throw new IllegalArgumentException("Invalid channel");
        }

        await(provider.setAsync(channel, "Waiting"));
        
        Optional<String> status = await(provider.watchAsync(channel));

        return status.isPresent() 
            ? new ResponseEntity<String>(status.get(), HttpStatus.OK)
            : new ResponseEntity<String>("Invalid status", HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/set")
    public ResponseEntity<String> set(@RequestParam String channel, @RequestParam String status) {
        if (StringUtils.isNullOrWhitespace(channel)) {
            throw new IllegalArgumentException("Invalid channel");
        }

        if (StringUtils.isNullOrWhitespace(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        await(provider.setAndNotifyAsync(channel, status));
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
