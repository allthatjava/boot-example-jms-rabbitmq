package brian.example.boot.jms.rabbitmq.listener.health;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqCon{
    private String username;
    private String password;
    private String url;
    private String protocol;
    private String port;
}
