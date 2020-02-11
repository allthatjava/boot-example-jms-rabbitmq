package brian.example.boot.jms.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class BootExampleJmsRabbitmqListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootExampleJmsRabbitmqListenerApplication.class, args);
    }
}
