package nuts.tech_learning_archive.event_driven.kafka_module.producer;


import model.StreamAvroModel;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaProducerConfig {

    @Bean
    KafkaProducer<String, StreamAvroModel> avroModelKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:19092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", "http://localhost:8081");
        props.put("auto.register.schemas", "false");
        props.put("retries", 2);
        return new KafkaProducer<>(props);
    }
}
