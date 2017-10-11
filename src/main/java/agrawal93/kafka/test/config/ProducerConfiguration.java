package agrawal93.kafka.test.config;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@EnableKafka
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@PropertySource("classpath:kafka.properties")
public class ProducerConfiguration {

    @Value("${schema.registry.url}")
    private String schemaRegistryUrl;

    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Bean
    public Map producerConfigurations() {
        Map configurations = new HashMap(kafkaConfiguration.getProducer());
        configurations.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        return configurations;
    }

    @Bean(name = {"keySerializer", "valueSerializer"})
    public Class defaultStringSerializer() {
        return StringSerializer.class;
    }

    @Bean(name = "transactionIdPrefix")
    public String defaultTransactionIdPrefix() {
        return "default.transaction.prefix.";
    }

    @Bean
    public ProducerFactory producerFactory(Class keySerializer, Class valueSerializer, String transactionIdPrefix) {
        Map configurations = producerConfigurations();
        configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getCanonicalName());
        configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getCanonicalName());

        DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(configurations);
        if (transactionIdPrefix != null) {
            producerFactory.setTransactionIdPrefix(transactionIdPrefix);
        }

        return producerFactory;
    }

    @Bean
    public KafkaTransactionManager kafkaTransactionManager(@Autowired ConfigurableBeanFactory beanFactory) {
        KafkaTransactionManager manager = new KafkaTransactionManager(producerFactory);
        manager.setFailEarlyOnGlobalRollbackOnly(true);
        manager.setNestedTransactionAllowed(true);
        manager.setValidateExistingTransaction(true);
        manager.setRollbackOnCommitFailure(true);
        manager.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);
        return manager;
    }

    @Bean
    public KafkaTemplate kafkaTemplate(ProducerFactory producerFactory) {
        return new KafkaTemplate(producerFactory);
    }

}
