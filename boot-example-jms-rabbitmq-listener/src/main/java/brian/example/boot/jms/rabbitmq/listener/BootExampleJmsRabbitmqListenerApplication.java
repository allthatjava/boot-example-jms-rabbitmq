package brian.example.boot.jms.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class BootExampleJmsRabbitmqListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootExampleJmsRabbitmqListenerApplication.class, args);
    }
}
