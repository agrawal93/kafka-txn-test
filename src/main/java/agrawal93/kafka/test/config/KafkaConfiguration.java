package agrawal93.kafka.test.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
@PropertySource("classpath:kafka.properties")
public class KafkaConfiguration {

    private final Map<String, String> producer = new HashMap<>();
    private final Map<String, String> consumer = new HashMap<>();

    public Map<String, String> getProducer() {
        return producer;
    }

    public Map<String, String> getConsumer() {
        return consumer;
    }
}
