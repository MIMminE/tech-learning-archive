# kafka_module

`kafka_module` 은 `Event Driven Microservices` 프로젝트 전반에서 카프카와 통신하는 모듈을 중앙에서 관리하기 위한 모듈이다.
어노테이션 기반 스프링 부트 자동설정을 지원하며, 토픽 생성, 프로듀서, 컨슈머 클라이언트 자동 빈 등록, `Avro 스키마 기반 데이터 직렬화` 등을 제공한다.

## Zookeeper

분산 시스템을 위한 중앙 집중식 코디네이터(조정자) 역할을 수행한다. 카프카는 내부적으로 주키퍼를 사용하여 토픽/파티션 메타데이터를 서로 공유하고, 리더 파티션을 선출하는 등을 수행한다.

분산 시스템을 사용하는 다양한 곳에서 사용되고 있으며 주키퍼 본인도 클러스터 구성되어 안전성을 확보하는 방식으로 사용된다. 직접적으로 애플리케이션에서 쓰기는 복잡하므로 카프카 등의 분산 시스템의 내부에서 사용되는 것
정도로만 이용되는 편이다.

시스템의 복잡성이 증가하기에 주키퍼 없이도 사용 가능한 형태의 카프카 모드가 등장하고는 있으나 현재까지는 주키퍼를 통한 방식이 표준처럼 사용되고 있다.

### Docker 설정

```yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:${KAFKA_VERSION:-latest}
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181 # 외부에서 주키퍼에 접근하기 위한 포트, 브로커나 스키마 레지스트리가 접근할 포트
    networks:
      - ${GLOBAL_NETWORK:-kafka}
```

## Schema Registry

Kafka 브로커와 통신하는 메시지 데이터의 `스키마(데이터 구조)`를 중앙에서 관리해주는 서비스이다. 주로 `Avro`, `Protobuf`, `JSON Schema` 와 같은 직렬화 포맷에서 사용된다.

메시지 스키마는 비즈니스가 변화하면서 자연스럽게 변경되는 경우가 많으며 프로듀서와 컨슈머를 독립적으로 개발하고 관리하는 메시지 시스템 특성상 각 개발팀이 변화된 스키마를 개별적으로 코드상에서의 수정이 필요하다.
이러한 상황에서 스키마가 중앙에서 관리되고 있다면 스키마 변경을 중앙에서 관리하기 때문에 전체 시스템 유지보수 측면에서 이점이 생긴다.

### 주요 역할

- `스키마 중앙 관리` : 데이터의 필드, 타입 등을 별도의 중앙 서버에 등록하고 관리한다. 주로 `생산자(Producer)`가 스키마를 등록하고 `소비자(Consumer)`가 스키마 정보를 참조하는 방식이다.
- `스키마 버전 관리` : 스키마가 변경될 때마다 버전을 관리하며, 과거 버전 스키마도 조회할 수 있다.
- `프로듀서 컨슈머 간 데이터 일관성 보장` : 프로듀서는 메시지 전송에 스키마 ID를 함께 보내며, 컨슈머는 스키마 ID를 통해 등록된 스키마로 메시지를 역직렬화한다.

### 카프카 스키마 레지스트리 REST API

프로듀서와 컨슈머는 스키마 레지스트리가 제공하는 REST API 를 통해 새로운 스키마를 등록하거나, 스키마 호환성 검사 등 여러 작업을 할 수 있다.
스키마 레지스트리에서 `subject`는 스키마를 그룹핑하여 버전 관리하는 논리적 단위이다. 주로 `{토픽이름-key}` 또는 `{토픽이름-value}` 형태로 쓰이는게 표준이며 커스텀도 가능하다.

#### Subject 조회

```shell
GET /subjects # 전체 subject 조회 
GET /subjects/{subject}/versions # 특정 subject의 모든 스키마 버전 조회
GET /subjects/{subject}/versions/latest # 특정 subject의 최신 스키마 조회
GET /subjects/{subject}/versions/{version} # 특정 subject의 특정 버전 조회
```

```shell
GET localhost:8081/subjects 

{
    "test-topic-value",
    "user-value",
    "group-value"
}
```

#### 스키마 등록 및 조회

```shell
POST /subjects/{subject}/versions # 새 스키마 등록, 요청 body에 스키마 JSON 를 포함하여 요청
GET /schemas/ids/{id} # 스키마 ID 로 스키마 조회
```

```shell
POST http://localhost:8081/subjects/my-topic-value/versions
Content-Type: application/vnd.schemaregistry.v1+json

{
  "schema": "{\"type\":\"record\",\"name\":\"User\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"}]}"
}
```

#### 스키마 호환성 검사

```shell
POST /compatibility/subjects/{subject}/versions/{version} # 새 스키마가 기존 스키마와 호환되는지 확인
```

```shell
POST http://localhost:8081/compatibility/subjects/my-topic-value/versions/latest
Content-Type: application/vnd.schemaregistry.v1+json

{
  "schema": "{\"type\":\"record\",\"name\":\"User\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"age\",\"type\":\"int\",\"default\":0}]}"
}
```

### Java Kafka Producer 에서의 스키마 레지스트리 설정 및 동작

자바 카프카 프로듀서는 직렬화 라이브러리로 Avro, Protobuf, JSON Schema 를 사용할 경우 내부적으로 스키마 레지스트리 REST API를 자동으로 호출해 등록, 조회, 캐싱 등을 처리한다.
`KafkaAvroSerializer`, `KafkaProtobufSerializer` 등 각 라이브러리에서 제공하는 시리얼라이저 클래스가 있다.

`KafkaProducer.sent` 메서드가 호출되면 시리얼라이저가 데이터 직렬화를 시도하며 이때, 스키마 레지스트리 서버와 통신하여 등록된 스키마인지를 확인한다.
만약, 스키마가 등록되어있지 않으면 프로듀서 설정에 따라 스키마를 신규로 등록하고 해당 스키마 아이디를 반환받아 카프카 브로커에게 전달한다.

프로듀서를 사용하는 입장에서 스키마 레지스트리 REST API 를 직접 호출하는 것은 대부분 하지 않는다. 개발자는 `schema.registry.url` 만 필수로 설정해주면 된다.
추가적으로 `auto.register.schemas` 값을 false 로 설정해주면 스키마 자동등록이 비활성화되어 존재하지 않는 스키마를 사용할 경우 예외가 발생한다.

자동 등록이 true(기본값)인 상태에서 이전에 등록했던 subject 에 대해 다른 스키마를 사용하려고 하면 내부적으로 스키마 호환성 검사를 자동으로 수행하게 된다.
즉, 같은 토픽에 대해 스키마 레지스트리에 등록된 스키마와 다른 메시지 데이터 구조를 사용하려고 할 때를 말한다. 호환이 되지 않을 경우에는 예외를 발생시킨다.

```yaml
# 스프링 부트 프로듀서 설정 예시
spring:
  kafka:
    properties:
      schema.registry.url: http://localhost:8081
      auto.register.schemas: false

    bootstrap-servers: localhost:19092, localhost:29092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
```

```java
// 빈 등록 방식을 사용하는 예시 
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
```

### Docker 를 통한 스키마 레지스트리 설정

```yaml
schema-registry:
  image: confluentinc/cp-schema-registry:${KAFKA_VERSION}
  container_name: schema-registry
  depends_on:
    - zookeeper
    - kafka-broker-1
    - kafka-broker-2
  ports:
    - "8081:8081"
  environment:
    SCHEMA_REGISTRY_HOST_NAME: schema-registry # 네트워크 상에서 사용할 호스트 이름
    SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: 'zookeeper:2181' # 메타데이터 저장소 위치 지정
    SCHEMA_REGISTRY_LISTENERS: http://schema-registry:8081 # 외부 요청을 받을 주소, 프로듀서와 컨슈머가 이곳으로 요청
  networks:
    - ${GLOBAL_NETWORK:-kafka}
```

## Kafka Broker

```yaml
kafka-broker-1:
  image: confluentinc/cp-kafka:${KAFKA_VERSION}
  container_name: kafka-broker-1
  ports:
    - "19092:19092"
  depends_on:
    - zookeeper
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-broker-1:9092,LISTENER_LOCAL://localhost:19092
    KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,LISTENER_LOCAL:PLAINTEXT
    KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
  networks:
    - ${GLOBAL_NETWORK:-kafka}
```
카프카는 여러 포트를 사용해 여러 리스너를 열어둘 수 있는데, 위 설정은 `컨테이너 내부 네트워크에서의 접근`과 `외부(호스트 PC 등)에서 브로커에 접근`하는 리스너를 달리 하기 위함의 도커 설정이 포함되어 있다.
포트를 분리함으로써 각 포트를 통한 통신에 대한 보안 정책(암호화 방식은 무엇을 쓸 것인지 등)을 달리할 수 있고, 여러 클라이언트에 하여금 권한 등을 제어할 수도 있다.


## 컨셉 및 핵심 소스 코드
main 진입점이 없는 스프링 부트 프로젝트로 설정한다. 카프카 프로듀서, 컨슈머, 어드민 클라이언트를 사용할 프로젝트에 의존성을 추가하여 사용하는 방식을 위해 라이브러리 방식으로 제공하기 위함이다.
```groovy
plugins {
    id 'java-library'
    id 'maven-publish'
}

dependencies {
    implementation platform('org.springframework.boot:spring-boot-dependencies:3.4.5')
    // 스프링 부트 BOM 사용을 위한 플러그인 대신 사용한다.  (io.spring.dependency-management 플러그인 대신)
    // 이를 통해 BOM이 적용된 의존성 관리가 여전히 가능하다.
//    ...
}
```

### avro 스키마 작성
Avro 프레임워크를 사용하는 스키마 레지스트리를 사용하므로 프로젝트 내부에 스키마를 작성해주어야 한다. 위치는 `main/avro/{스키마 이름}.avsc` 경로가 기본이다.
빌드 툴에 적절한 플러그인 설정을 해주면 빌드 과정에서 스키마 클래스가 자동으로 생성되고 카프카 통신에 사용할 수 있게 된다.
```json
{
  "namespace": "model",
  "type": "record",
  "name": "StreamAvroModel",
  "fields": [
    { "name": "userId", "type": "long" },
    { "name": "id", "type": "long" },
    { "name": "text", "type": ["null", "string"] },
    {
      "name": "createdAt",
      "type": ["null", "long"],
      "logicalType": ["null", "date"]
    }
  ]
}
```

### 