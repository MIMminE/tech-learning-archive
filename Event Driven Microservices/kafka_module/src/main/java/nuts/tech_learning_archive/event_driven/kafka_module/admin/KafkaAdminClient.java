package nuts.tech_learning_archive.event_driven.kafka_module.admin;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;

@RequiredArgsConstructor
public class KafkaAdminClient {

    private final AdminClient adminClient;

    public void createTopic(String topicName, int partitions, short replicationFactor) {
        try {
            adminClient.createTopics(List.of(new NewTopic(topicName, partitions, replicationFactor)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
