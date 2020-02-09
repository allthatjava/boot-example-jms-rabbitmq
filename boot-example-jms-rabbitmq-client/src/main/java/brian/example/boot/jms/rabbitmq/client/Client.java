package brian.example.boot.jms.rabbitmq.client;

import brian.example.boot.jms.rabbitmq.client.config.RabbitJmsConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Client implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    public Client(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Sending message...");

        MessageProperties msgProp = new MessageProperties();
        msgProp.setCorrelationId(UUID.randomUUID().toString());
        msgProp.setConsumerQueue(RabbitJmsConfig.QUEUE_NAME);
        Message msg = new Message("Hello".getBytes(), msgProp);

        Message res = rabbitTemplate.sendAndReceive(RabbitJmsConfig.TOPIC_EXCHANGE_NAME, "foo.bar.baz", msg);

        System.out.println("res:"+new String(res.getBody()));

        Thread.sleep(3000);
    }
}
