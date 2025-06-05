package nuts.tech_learning_archive.event_driven.mock_stream_generator.producer;

import lombok.RequiredArgsConstructor;
import model.StreamAvroModel;
import nuts.tech_learning_archive.event_driven.kafka_module.admin.KafkaAdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerRunner implements CommandLineRunner {

    private final KafkaProducer<String, StreamAvroModel> avroModelKafkaProducer;
    private final KafkaAdminClient kafkaAdminClient;

    @Override
    public void run(String... args) throws Exception {
        kafkaAdminClient.createTopic("testTopic",1, (short) 1);
        StreamAvroModel avroModel = new StreamAvroModel(1L, 2L, "hello", 12L);
        avroModelKafkaProducer.send(new ProducerRecord<>("dsdsds1", avroModel));
    }
}
