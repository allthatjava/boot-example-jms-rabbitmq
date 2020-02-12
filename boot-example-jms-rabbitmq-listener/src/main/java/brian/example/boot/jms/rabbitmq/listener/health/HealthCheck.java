package brian.example.boot.jms.rabbitmq.listener.health;

import brian.example.boot.jms.rabbitmq.listener.config.RabbitJmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Slf4j
@Component
public class HealthCheck implements HealthIndicator {

    @Autowired private RestTemplate restTemplate;
    @Autowired private MqCon mqCon;

    private RabbitTemplate rabbitTemplate;

    public HealthCheck(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Check if RabbitMQ is up by sending a dummy message
     * @return
     */
    @Override
    public Health health() {

//        boolean isUp = checkRabbitMq();
        boolean isUp = sentDummyMessage();

        if( isUp ) {
            return Health.up().build();
        }else{
            return Health.down().build();
        }
    }

    /**
     * Send Dummy message for Health check
     *
     * @return true if successfully sent dummy message to RabbitMQ
     */
    private boolean sentDummyMessage(){
        boolean isUp = true;
        try {
            rabbitTemplate.convertAndSend(RabbitJmsConfig.EXCHANGE_NAME,"HealthCheck");
//            MessageProperties msgProp = new MessageProperties();
//            msgProp.setCorrelationId(UUID.randomUUID().toString());
//            msgProp.setConsumerQueue(RabbitJmsConfig.QUEUE_NAME);
//            Message msg = new Message("HealthCheck".getBytes(), msgProp);
//
//            Message res = rabbitTemplate.sendAndReceive(RabbitJmsConfig.TOPIC_EXCHANGE_NAME,
//                    "foo.bar.baz", msg);
        }catch(AmqpException e){
            isUp = false;
        }

        return isUp;
    }

    /**
     * Request RabiitMQ node info for Health Check
     * @return
     */
    private boolean checkRabbitMq(){
        String username = mqCon.getUsername();
        String password = mqCon.getPassword();
        String url = mqCon.getUrl();
        String protocol = mqCon.getProtocol();
        String port = mqCon.getPort();
        if(port != null) port = ":"+port;
        else port = "";
        log.info(username+":"+password+"@"+url+"-"+protocol+":"+port);

        // create auth credentials
        String authStr = username+":"+password;
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        // create request
        HttpEntity request = new HttpEntity(headers);

        // If MQ is up, health ok.
        ResponseEntity<String> response = restTemplate.exchange(protocol+"://"+url+port+"/api/nodes",
                HttpMethod.GET, request, String.class);

        return HttpStatus.OK.equals(response.getStatusCode());
    }
}
