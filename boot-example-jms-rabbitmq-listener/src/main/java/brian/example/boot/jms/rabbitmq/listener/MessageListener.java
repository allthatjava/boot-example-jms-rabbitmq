package brian.example.boot.jms.rabbitmq.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @RabbitListener(queues = {"brian-queue"})
    public Message receiveMessage(final Message message) {

        String received = new String(message.getBody());
        MessageProperties msgProp = message.getMessageProperties();

        MessageProperties newProp = new MessageProperties();
        newProp.setReplyTo( msgProp.getReplyTo() );
        newProp.setCorrelationId(msgProp.getCorrelationId());

        return new Message(("Received: "+received).getBytes(), newProp);
    }
}
