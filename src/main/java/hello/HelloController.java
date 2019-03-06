package hello;

import org.springframework.web.bind.annotation.RestController;
import hello.models.Result;
import hello.models.Transaction;
import hello.models.providers.Provider;
import hello.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
import java.util.Locale.Category;

import static com.ea.async.Async.await;

@RestController
@Api(value="/api/transaction", description="Customer Profile", produces ="application/json")
public class HelloController {
    @Autowired
    Provider provider;

    @RequestMapping("/")
    public String index() {
        return "Spring API with Observer pattern";
    }

    @ApiOperation(value="Start a new transaction", response = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 404, message = "Not Found"),
        @ApiResponse(code = 500, message = "Timeout reached")
    })
    @PostMapping("/api/transaction")
    public ResponseEntity<String> start(@RequestBody Transaction transaction) {
        Result result = isValidTransaction(transaction);

        if (!result.isValid()) {
            throw new IllegalArgumentException(result.getErrorMessage());
        }

        await(provider.setAsync(transaction));
        
        Optional<String> status = await(provider.watchAsync(transaction));

        return status.isPresent() 
            ? new ResponseEntity<String>(status.get(), HttpStatus.OK)
            : new ResponseEntity<String>("Invalid status", HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value="Update a transaction status", response=Transaction.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", 
            examples = @Example(value = { 
                @ExampleProperty(value = "Finished", mediaType = "application/json") 
            })),
            
    })
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

        if (transaction == null) {
            result.setValid(false);
            result.setErrorMessage("Invalid transaction");
            return result;
        }

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

        if (transaction.getTimeoutSeconds() <= 0) {
            result.setValid(false);
            result.setErrorMessage("Invalid request timeout");
            return result;
        }

        return result;
    }
}
