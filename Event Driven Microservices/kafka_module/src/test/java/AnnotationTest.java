import nuts.tech_learning_archive.event_driven.kafka_module.annotation.EnableKafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;


@ContextConfiguration(classes = {KafkaProducerConfiguration.class, AnnotationTest.TestConfig.class})
@SpringBootTest
public class AnnotationTest {

    @Autowired
    private KafkaProducerConfiguration kafkaProducerConfiguration;


    @Test
    void testBean() {
        System.out.println(kafkaProducerConfiguration.getKafkaProperties().getProperties());

        DefaultKafkaProducerFactory<Object, Object> factory = new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration.getKafkaProperties().buildConsumerProperties());
        System.out.println(factory);
        Producer<Object, Object> producer = factory.createProducer();
        producer.send(new ProducerRecord<>("topic","ss"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public KafkaProperties kafkaProperties() {
            // 테스트용 KafkaProperties 객체 생성 및 반환
            KafkaProperties kafkaProperties = new KafkaProperties();
            Map<String, String> props = kafkaProperties.getProperties();
            props.put("schema.registry.url", "http://localhost:8081");
            props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
            props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            return kafkaProperties;
        }
    }
}
