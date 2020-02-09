package brian.example.boot.jms.rabbitmq.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootExampleJmsRabbitmqClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootExampleJmsRabbitmqClientApplication.class, args).close();
    }

}
