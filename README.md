# Getting Started


### Using Docker Imagge for RabbitMQ
```
docker pull rabbitmq
docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

## boot-example-jms-rabbit-listener

#### Enable RabbitMQ
Need to add `@EnableRabbit` annotation as a part of configuration to use RabbitMQ.

#### Listener
By using MessageProperties that received, the listener will send the message back to sender.
Reply-To and Correlation-Id is used for returning message.
```java
public class MessageListener {
...
    @RabbitListener(queues = {RabbitJmsConfig.QUEUE_NAME})
    public Message receiveMessage(final Message message) {
...
    MessageProperties newProp = new MessageProperties();
    newProp.setReplyTo( msgProp.getReplyTo() );
    newProp.setCorrelationId(msgProp.getCorrelationId());
...
    return new Message(returningMessage.getBytes(), newProp);
}
```

#### Configuration in properties
Configuration that is need to use RabbitMQ
```java
---
spring:
  rabbitmq:
    host: 192.168.99.100
    port: 5672
    username: guest
    password: guest
```

#### Health Check
You can do health check of this application by the url ```http://localhost:8081/actuator/health```.
This health check will send a dummy message to RabbitMQ to see if RabiitMQ is up.
```java
    private boolean sentDummyMessage(){
        boolean isUp = true;
        try {
            rabbitTemplate.convertAndSend(RabbitJmsConfig.TOPIC_EXCHANGE_NAME,"HealthCheck");
        }catch(AmqpException e){
            isUp = false;
        }
        return isUp;
    }
```


## boot-example-jms-rabbitmq-client
Use __RabbitTemplate__ to send a message and receive message back from RabbitMQ.
```java
    @GetMapping( "/hello/{name}" )
    public String sendHello(@PathVariable("name") String name) {
    ...
        MessageProperties msgProp = new MessageProperties();
        msgProp.setCorrelationId(UUID.randomUUID().toString());
        msgProp.setConsumerQueue(RabbitJmsConfig.QUEUE_NAME);

        Message msg = new Message(name.getBytes(), msgProp);
        Message res = rabbitTemplate.sendAndReceive(RabbitJmsConfig.TOPIC_EXCHANGE_NAME,
                                            "foo.bar.baz", msg);
    ...
    }
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/maven-plugin/)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/reference/htmlsingle/#boot-features-amqp)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)

