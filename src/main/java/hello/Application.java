package hello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import hello.models.providers.Provider;
import hello.models.providers.RedisProvider;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public Provider getTransformerBean() {
        return new RedisProvider("localhost", 6379);
    }
}
