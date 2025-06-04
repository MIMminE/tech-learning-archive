package nuts.tech_learning_archive.event_driven.mock_stream_generator;

import nuts.tech_learning_archive.event_driven.kafka_module.admin.EnableKafkaAdmin;
import nuts.tech_learning_archive.event_driven.kafka_module.producer.EnableKafkaProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafkaProducer
@EnableKafkaAdmin
public class MockStreamGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockStreamGeneratorApplication.class, args);
	}

}
