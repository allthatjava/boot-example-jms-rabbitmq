package brian.example.boot.jms.rabbitmq.listener.config;

import brian.example.boot.jms.rabbitmq.listener.health.MqCon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@EnableRabbit
@Configuration
public class RabbitJmsConfig {

    public static final String TOPIC_EXCHANGE_NAME = "brian-topic-exchange";
    public static final String QUEUE_NAME = "brian-queue";

    @Autowired
    private Environment env;

    //===> Required to create non-existing Exchange/Queue =============>
    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }
    //<===================================================================




    //===> Optional Config ============================================>
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Profile("default")
    @Bean
    public MqCon getLocalMqCon(){
        return new MqCon(
                env.getProperty("spring.rabbitmq.username"),
                env.getProperty("spring.rabbitmq.password"),
                env.getProperty("spring.rabbitmq.host"),
                env.getProperty("brian.mq.protocol"),
                env.getProperty("brian.mq.port")
        );
    }

    @Profile({"development","cloud"})
    @Bean
    public MqCon getCloudMqCon(){

        log.info("VCAP services info ======> "+System.getenv("VCAP_SERVICES"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode vcapServices = null;
        try {
            vcapServices = mapper.readTree(System.getenv("VCAP_SERVICES"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String str = vcapServices.get("cloudamqp").get(0).get("credentials").get("http_api_uri").toString();
        log.info("str============>:"+str);

        Pattern pattern = Pattern.compile("(\")(.*)(:\\/\\/)(.*)(:)(.*)(@)(.*)(\\/api)");
        Matcher matcher = pattern.matcher( str );
        matcher.lookingAt();
        String username = matcher.group(4);
        String password = matcher.group(6);
        String url = matcher.group(8);
        String protocol = matcher.group(2);
        String port = env.getProperty("brian.mq.port");

        log.info("username:"+username+",password:"+password+",url:"+url+",protocol:"+protocol+",port:"+port );

        return new MqCon(username,password,url,protocol,port);
    }
    //<=======================================================
}
