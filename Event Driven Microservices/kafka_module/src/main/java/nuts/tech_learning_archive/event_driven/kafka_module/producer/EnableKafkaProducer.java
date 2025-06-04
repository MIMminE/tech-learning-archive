package nuts.tech_learning_archive.event_driven.kafka_module.producer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(KafkaProducerConfig.class)
public @interface EnableKafkaProducer {
}
