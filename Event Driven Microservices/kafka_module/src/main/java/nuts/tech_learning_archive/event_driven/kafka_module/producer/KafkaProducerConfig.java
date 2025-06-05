package nuts.tech_learning_archive.event_driven.kafka_module.producer;


import lombok.RequiredArgsConstructor;
import model.StreamAvroModel;
import nuts.tech_learning_archive.event_driven.kafka_module.properties.KafkaAvroProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@Import(KafkaAvroProducerProperties.class)
public class KafkaProducerConfig {

    private final KafkaAvroProducerProperties kafkaProducerProperties;

    @Bean
    KafkaProducer<String, StreamAvroModel> avroModelKafkaProducer() {
        List<String> bootstrapServers = kafkaProducerProperties.getBootstrapServers();
        System.out.println(bootstrapServers);
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
