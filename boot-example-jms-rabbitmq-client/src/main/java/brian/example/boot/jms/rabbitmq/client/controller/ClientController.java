package brian.example.boot.jms.rabbitmq.client.controller;

import brian.example.boot.jms.rabbitmq.client.config.RabbitJmsConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ClientController {

    private final RabbitTemplate rabbitTemplate;

    public ClientController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping( "/hello/{name}" )
    public String sendHello(@PathVariable("name") String name) {

        System.out.println("Web Sending message...");

        MessageProperties msgProp = new MessageProperties();
        msgProp.setCorrelationId(UUID.randomUUID().toString());
        msgProp.setConsumerQueue(RabbitJmsConfig.QUEUE_NAME);

        Message msg = new Message(name.getBytes(), msgProp);

        Message res = rabbitTemplate.sendAndReceive(RabbitJmsConfig.TOPIC_EXCHANGE_NAME,
                                            "foo.bar.baz", msg);

        String retStr = "";
        if( res != null )
            retStr = "Web res:"+new String(res.getBody());
        else
            retStr = "Web res is null";

        System.out.println(retStr);

        return retStr;
    }

    @GetMapping( "/hello" )
    public String hello(){
        return "Hello";
    }
}
