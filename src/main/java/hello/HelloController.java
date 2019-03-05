package hello;

import org.springframework.web.bind.annotation.RestController;
import hello.models.Result;
import hello.models.Transaction;
import hello.models.providers.Provider;
import hello.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/api/transaction")
    public ResponseEntity<String> start(@RequestBody Transaction transaction) {
        Result result = isValidTransaction(transaction);

        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        await(provider.setAsync(transaction));
        
        Optional<String> status = await(provider.watchAsync(transaction.getId()));

        return status.isPresent() 
            ? new ResponseEntity<String>(status.get(), HttpStatus.OK)
            : new ResponseEntity<String>("Invalid status", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/api/transaction")
    public ResponseEntity<String> set(@RequestBody Transaction transaction) {
        Result result = isValidTransaction(transaction);

        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }
        
        await(provider.setAndNotifyAsync(transaction));
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private Result isValidTransaction(Transaction transaction) {
        Result result = new Result();

        if (StringUtils.isNullOrWhitespace(transaction.getId())) {
            result.setValid(false);
            result.setErrorMessage("Invalid transaction ID");
            return result;
        }

        if (StringUtils.isNullOrWhitespace(transaction.getStatus())) {
            result.setValid(false);
            result.setErrorMessage("Invalid status");
            return result;
        }

        return result;
    }
}
