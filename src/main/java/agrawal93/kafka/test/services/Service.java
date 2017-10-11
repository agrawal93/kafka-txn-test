package agrawal93.kafka.test.services;

import agrawal93.kafka.test.exceptions.TopicNotFoundException;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import kafka.admin.AdminUtils;
import kafka.utils.ZkUtils;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    public Service(@Autowired BeanFactory beanFactory) {
        ProducerFactory producerFactory = beanFactory.getBean(ProducerFactory.class, KafkaAvroSerializer.class, KafkaAvroSerializer.class, null);
        KafkaTransactionManager kafkaTransactionManager = beanFactory.getBean(KafkaTransactionManager.class, producerFactory);
        kafkaTemplate = beanFactory.getBean(KafkaTemplate.class, producerFactory);
    }

    private final String avroSchemaAsString = "{\"name\":\"message\",\"namespace\":\"test_message\",\"type\":\"record\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"}]}";
    private final Schema avroSchema = new Schema.Parser().parse(avroSchemaAsString);

    @Autowired
    private ZkUtils zkUtils;

    @Transactional
    public void process() {
        String topic = "test_1";
        for (int i = 1; i < 5; i++) {
            GenericRecord record = new GenericData.Record(avroSchema);
            record.put("id", "id-1." + i);
            record.put("message", "Message-1." + i);
            if (AdminUtils.topicExists(zkUtils, topic)) {
                kafkaTemplate.send(topic, record);
            } else {
                throw new TopicNotFoundException("Unable to publish to Kafka. Topic [" + topic + "] does not exist.");
            }
        }

        topic = "test_2";
        for (int i = 1; i < 5; i++) {
            GenericRecord record = new GenericData.Record(avroSchema);
            record.put("id", "id-2." + i);
            record.put("message", "Message-2." + i);
            if (AdminUtils.topicExists(zkUtils, topic)) {
                kafkaTemplate.send(topic, record);
            } else {
                throw new TopicNotFoundException("Unable to publish to Kafka. Topic [" + topic + "] does not exist.");
            }
        }
    }

    @KafkaListener(topicPattern = "test_.", containerFactory = "keyAvro-valueAvro")
    public void receivedMessage(@Payload GenericRecord record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName) {
        System.out.println("Messaged received from topic [" + topicName + "]: " + record.toString());
    }

}
