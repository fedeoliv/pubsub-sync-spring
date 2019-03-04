package hello;

import java.util.Arrays;

import com.ea.async.Async;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }


    // @Bean
    // public RedisProvider getTransformerBean() {
    //     return new RedisProvider("localhost", 6379);
    // }

    // @Bean
    // MessageListenerAdapter messageListener() { 
    //     return new MessageListenerAdapter(new RedisMessageSubscriber());
    // }

    // @Bean
    // JedisConnectionFactory jedisConnectionFactory() {
    //     return new JedisConnectionFactory();
    // }
    
    // @Bean
    // public RedisTemplate<String, Object> redisTemplate() {
    //     RedisTemplate<String, Object> template = new RedisTemplate<>();
    //     template.setConnectionFactory(jedisConnectionFactory());
    //     return template;
    // }

    // @Bean
    // RedisMessageListenerContainer redisContainer() {
    //     RedisMessageListenerContainer container = new RedisMessageListenerContainer(); 
    //     container.setConnectionFactory(jedisConnectionFactory()); 
    //     container.addMessageListener(messageListener(), topic()); 
    //     return container; 
    // }

    // @Bean
    // MessagePublisher redisPublisher() { 
    //     return new RedisMessagePublisher(redisTemplate(), topic());
    // }

    // @Bean
    // ChannelTopic topic() {
    //     return new ChannelTopic("messageQueue");
    // }
}
