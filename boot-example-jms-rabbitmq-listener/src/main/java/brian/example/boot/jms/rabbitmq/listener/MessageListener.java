package brian.example.boot.jms.rabbitmq.listener;

import brian.example.boot.jms.rabbitmq.listener.config.RabbitJmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener {

    /**
     * This listener will return the message that has been received
     *
     * @param message
     * @return
     */
    @RabbitListener(queues = {RabbitJmsConfig.QUEUE_NAME})
    public Message receiveMessage(final Message message) {

        String received = new String(message.getBody());
        MessageProperties msgProp = message.getMessageProperties();

        MessageProperties newProp = new MessageProperties();
        newProp.setReplyTo( msgProp.getReplyTo() );
        newProp.setCorrelationId(msgProp.getCorrelationId());

        String returningMessage ="Received: "+received;
        log.info(returningMessage);

        return new Message(returningMessage.getBytes(), newProp);
    }
}
