package agrawal93.kafka.test.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@EnableKafka
@Configuration
@PropertySource("classpath:kafka.properties")
public class ConsumerConfiguration {

    @Value("${consumer.count}")
    private Integer consumerCount;

    @Value("${schema.registry.url}")
    private String schemaRegistryUrl;

    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Bean
    public Map consumerConfigurations() {
        return new HashMap(kafkaConfiguration.getConsumer());
    }

    @Bean(name = "keyAvro-valueAvro")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory_AvroAvro() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(consumerCount == null ? 3 : consumerCount);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigurations(), avroKeyDeserializer(), avroValueDeserializer()));
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
   
    @Bean
    public Deserializer avroKeyDeserializer() {
        Map<String, Object> props = new HashMap<>();
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        Deserializer deserializer = new KafkaAvroDeserializer();
        deserializer.configure(props, true);
        return deserializer;
    }

    @Bean
    public Deserializer avroValueDeserializer() {
        Map<String, Object> props = new HashMap<>();
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        Deserializer deserializer = new KafkaAvroDeserializer();
        deserializer.configure(props, false);
        return deserializer;
    }
    
}
