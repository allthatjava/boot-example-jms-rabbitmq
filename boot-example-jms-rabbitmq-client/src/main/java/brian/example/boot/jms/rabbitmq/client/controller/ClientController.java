package brian.example.boot.jms.rabbitmq.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class ClientController {

    public static final String EXCHANGE_NAME = "brian-exchange";
    public static final String QUEUE_NAME = "brian-queue";

    private final RabbitTemplate rabbitTemplate;

    public ClientController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping( "/hello/{name}" )
    public String sendHello(@PathVariable("name") String name) {

        log.info("Web Sending message...");

        MessageProperties msgProp = new MessageProperties();
        msgProp.setCorrelationId(UUID.randomUUID().toString());
        msgProp.setConsumerQueue(QUEUE_NAME);

        Message msg = new Message(name.getBytes(), msgProp);
        Message res = rabbitTemplate.sendAndReceive(EXCHANGE_NAME,
                                            "foo.bar.1", msg);

        String retStr = "";
        if( res != null )
            retStr = "Web res:"+new String(res.getBody());
        else
            retStr = "Web res is null";

        log.info(retStr);

        return retStr;
    }

    /**
     * This is to see VCAP service info on PCF
     * @return
     */
    @GetMapping("/vcap")
    public String vcap(){
        return System.getenv("VCAP_SERVICES");
    }

    @GetMapping( "/hello" )
    public String hello(){
        return "Hello";
    }
}
