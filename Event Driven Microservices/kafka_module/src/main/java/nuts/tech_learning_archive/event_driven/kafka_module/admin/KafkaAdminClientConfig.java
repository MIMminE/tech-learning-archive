package nuts.tech_learning_archive.event_driven.kafka_module.admin;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaAdminClientConfig {

    @Bean
    KafkaAdminClient kafkaAdminClient() {
        try {
            return new KafkaAdminClient(AdminClient.create(Map.of(
                    CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092"
            )));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
