package nuts.tech_learning_archive.event_driven.kafka_module.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-avro-producer")
public class KafkaAvroProducerProperties {

    private List<String> bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String schemaRegistryUrl;
    private Boolean autoRegisterSchemas;
    private Integer retries;
}

