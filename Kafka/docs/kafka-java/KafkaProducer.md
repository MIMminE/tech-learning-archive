# JAVA KafkaProducer API

## KafkaProducer 인스턴스 생성

```java
public void createKafkaProducer() {
    Properties props = new Properties();
    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.56.101:9092");
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.setProperty(ProducerConfig.ACKS_CONFIG, "0"); 

    try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props)) {
        // 카프카 프로듀서 로직...
    }
}
```

**카프카 프로듀서 주요 Properties**

| **설정값**                                 | **설명**                         | **기본값**           | **필수/선택** |
|-----------------------------------------|--------------------------------|-------------------|-----------|
| `bootstrap.servers`                     | Kafka 브로커 주소                   | 없음 (필수)           | 필수        |
| `key.serializer`                        | 메시지 키 직렬화 클래스                  | 없음                | 권장        |
| `value.serializer`                      | 메시지 값 직렬화 클래스                  | 없음 (필수)           | 필수        |
| `acks`                                  | 브로커의 확인 응답 수준                  | `1`               | 선택        |
| `retries`                               | 메시지 전송 실패 시 재시도 횟수             | 무제한               | 선택        |
| `batch.size`                            | 배치 크기 (바이트 단위)                 | `16384` (16KB)    | 선택        |
| `linger.ms`                             | 배치를 채우기 위해 기다리는 시간 (밀리초)       | `0`               | 선택        |
| `buffer.memory`                         | 프로듀서 버퍼 메모리 크기                 | `33554432` (32MB) | 선택        |
| `compression.type`                      | 메시지 압축 방식                      | `none`            | 선택        |
| `client.id`                             | 프로듀서를 식별하기 위한 ID               | 없음                | 선택        |
| `enable.idempotence`                    | 멱등성 활성화                        | `false`           | 선택        |
| `max.in.flight.requests.per.connection` | 하나의 연결에서 동시에 보낼 수 있는 요청의 최대 개수 | `5`               | 선택        |

## 메시지 전송

생성된 카프카 프로듀서 인스턴스를 통해 브로커에 메시지를 전송하기 위해서는 `send` 메서드를 사용한다. ProducerRecord 객체를 인자로 받으며

```java
private void sendProducerRecord(Properties properties) {
    
    try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("topicName", "hello world");

        Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send(producerRecord);
        // 레코드 정의되어 있는 토픽으로 메시지를 전달한다. 
                
        kafkaProducer.send(producerRecord, (recordMetadata, e) -> 
            System.out.println(recordMetadata.partition() + " " + recordMetadata.offset())
        );
        // 레코드와 함께 Ack 응답에 반응하는 콜백 메서드를 함께 제공해준다.
    }
}
```

이때 구현하는 콜백 메서드는 카프카 라이브러리가 제공하는 인터페이스의 구현체이다.
```java
public interface Callback {
    void onCompletion(RecordMetadata var1, Exception var2);
    // RecordMetadata : 브로커부터 전달되는 응답
    // Exception : 전송에 실패했을 경우 받는 예외
```

프로듀서가 브로커부터 메시지를 전송하면, 프로듀서의 설정(`acks`)에 따라 브로커는 메시지를 제대로 받았는지를 프로듀서 측에게 알리도록 되어 있다.
send 메서드의 응답으로 Future 객체를 받도록 되어 있는데, 비동기로 전달되는 브로커의 응답을 처리하기 위한 것이다.

만약 보내고자 하는 레코드와 함께 콜백 구현체를 함께 인자로 전달한다면 비동기적으로 들어오는 브로커의 응답에 반응하여 메서드를 실행할 수 있게 된다. 
콜백을 사용하는 것이 일반적으로 사용되는 패턴이라고 볼 수 있다. 

acks 옵션은 카프카 설계상 프로듀서 측에서 지정하도록 되어 있으며 프로듀서-브로커 간에 성능, 내구성 중 어느 요구사항을 더 충족할 지를 결정하게 되는 부분이다.
브로커 측이 아닌 프로듀서 측의 설정으로 결정한 것은 같은 브로커에 연결되었더라도 각 프로듀서마다 요구사항을 유연하게 변경할 수 있도록 하기 위함이다.

### 프로듀서 동기식 처리
카프카 프로듀서 클라이언트의 send 메서드는 비동기 메서드이지만 Future 클래스의 특징을 활용하면 메시지 전송과 응답 받기를 동기식으로 처리할 수 있다.
```java
RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get();
// RecordMetadata 는 브로커에게서 전달받은 응답 메시지이다.
```
이 코드는 브로커로부터 Ack 메시지를 받을 때까지 현재 스레드를 대기 상태로 만든다. 
단, 이런식의 동기식 처리는 카프카 시스템의 장점을 모두 사라지게 만드는 것이므로 테스트 환경에서만 진행해야 한다. 


```java
Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send(producerRecord);

// 내부 버퍼의 메시지를 브로커로 전송하는 요청을 보냄 (실제 네트워크로 전달되지는 않음)
kafkaProducer.flush();

// 프로듀서 종료
kafkaProducer.close();
```
send 메서드가 실제 브로커로 네트워크 전송까지 수행하는 것은 아니다. send 메서드는 비동기적으로 별도 스레드에서 관리되는 `네트워크 전송 스레드`에게
메시지를 전달하기만 한다. 네트워크 전송 스레드는 전달받은 메시지를 프로듀서 설정과 브로커 메타 정보를 가지고 실제 네트워크에게 전송할 조건을 만족하면 전송한다.

위 코드에서 send 메서드를 호출하더라도 네트워크 전송 조건에 충족하지 않았을 때는 메시지를 전송하지 않을 수 있으며, `flush` 메서드를 호출하면 네트워크 전송 스레드에게
강제로 현재 전송 대기중인 메시지들을 브로커에서 전송하도록 할 수 있다.

### Key 값을 가지는 메시지 전송
카프카 프로듀서의 메시지에 해당하는 `ProducerRecord` 인스턴스 생성에 키를 포함시켜주면 브로커에 `Key-Value` 형태로 메시지를 전송할 수 있다.
```java
ProducerRecord<String, String> recordUsingKey = new ProducerRecord<>("topicName", "key", "value"); 
kafkaProducer.send(recordUsingKey);
```
주의할 점은 제너릭을 통해 Key-Value 타입을 정확하게 명시해주어야 한다.

```text
카프카 메시지에 키를 함께 보냄으로 같은 키를 사용하는 메시지들을 하나의 파티션으로만 보낼 수 있고
이를 통해 같은 주체가 발생시킨 메시지들을 시간의 순서대로 파티션에 쌓이게 할 수 있다.
```

### 커스텀 콜백 구현
브로커로부터 ack 응답을 받을 때 실행되도록 하기위해 send 메서드에 전달하는 Callback 구현체를 만들어두고 재사용하는 방법이다.
```java
@Slf4j
public class KafkaProducerCallback implements Callback {

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e == null) {
            log.info("Successfully sent data to kafka");
            log.info(recordMetadata.toString());
        } else {
            log.error(e.getMessage(), e);
        }
    }
}
```

### 프로듀서 메시지 배치 전송에 대한 이해

카프카 프로듀서는 효율적인 메시지 전송을 위해 `배치 전송(batch sending)` 메커니즘을 사용한다. 이 과정에서 `어큐멀레이터(Accumulator)`와 `파티셔너(Partitioner)`가 중요한 역할은 한다.

**어큐멀레이터**는 프로듀서가 전송하려는 메시지를 파티션 단위로 메모리에 저장해두고, 특정 조건이 충족되었을 때 브로커로 전송하는 역할을 한다. 여기서 말하는 특정 조건이라 함은
일정 크기의 `메시지 크기(batch.size)`에 도달하거나, `일정 시간(linger.ms)`이 지나면 해당 배치 내의 메시지를 묶어 브로커로 전송한다. 이는 네트워크 전송 효율을 높일 수 있는 방법이다.


**파티셔너**는 프로듀서가 메시지 전송 요청을 하면 해당 메시지를 어떤 파티션으로 전송할 지를 결정하는 역할을 한다. 메시지에 키가 있는 경우에는 키를 해싱하여 같은 키는 같은 파티션으로 전송되도록 조절한다.
파티셔너에 의해 메시지가 어떤 파티션에게 전송될지가 결정되면 해당 메시지를 어큐멀레이터에게 전달하고 결과적으로 어큐멀레이터가 적절한 파티션 버퍼에 적재한다.

배치 단위로 네트워크를 이용하므로 `네트워크 처리량(Throughput)`이 증가하고 네트워크 호출가 줄어들기 때문에 성능 자체에는 긍정적일 수 있으나,
설정을 어떻게 하는지에 따라 `전송 지연(Latency)`가 증가할 수 있다.

```text
자바 라이브러리 상에서 어큐멀레이터에에서 보관하고 있던 메시지 배치를 실제 보내는 주체는 별도의 Sender 스레드이다.
어큐멀레이터와 파티셔너는 메시지를 어떤 배치에 적재하는 지에 대한 작업만 하므로 메인스레드를 공유하며
실제 네트워크 통신이 필요한 부분은 별도의 스레드에서 비동기로 이루어진다.
```

### 프로듀서 재전송 관련 파라미터 이해

카프카 프로듀서는 전송의 성능, 신뢰성 수준을 조절하기 위해 여러 재전송 관련 파라미터를 제공하고 있다.

- `max.block.ms` : 프로듀서가 send 요청을 했으나 어큐멀레이터 메모리가 부족해 block 상태로 넘어간 상태를 유지할 수 있는 시간, 시간 초과시 예외 발생
- `linger.ms` : sender 스레드가 어큐멀레이터의 각 배치별 기다릴 수 있는 최대 시간, 지정 시간 도달 전까지 전송이 안되면 가져가서 전송 
- `request.timeout.ms` : sender 스레드가 브로커에서 전송을 하고 응답을 기다릴 수 있는 최대 시간, 시간 초과시 리트라이 또는 예외 발생
- `retries` : retry 재시도 최대 횟수, 기본값은 0으로 별도 지정하지 않으면 리트라이 비활성화
- `retry.backoff.ms` : retry 재시도 주기
- `delivery.timeout.ms` : 프로듀서의 전송 API가 호출된 후, 브로커로 전송에 성공하거나 실패하는 결과를 도출하기 까지의 최대 허용 시간, 초과시 예외 발생

`delivery.timeout.ms`의 값은 `linger.ms`와 `request.timeout.ms`의 합보다 크거나 같아야 한다. 만약 delivery.timeout.ms가 더 작아버리면 정상적인 시간 내에 처리할 수 있는 요청도 실패로 간주될 가능성이 있다.

```text
프로듀서 재전송에서 재전송 횟수에 해당하는 retries 옵션을 굉장히 크게 설정해두고 
delivery.timeout.ms (기본값 120000, 2분) 값을 조절하는 식의 사용법을 권장한다.
```

### 프로듀서 병렬 요청
`max.in.flight.requests.per.connection` 옵션은 프로듀서 연결 당 동시에 할 수 있는 요청의 최대 개수(기본값 5)를 의미한다. 이는 프로듀서 병렬 전송 성능에 직접적인 영향을 끼친다.

프로듀서는 어큐멀레이터에서 관리되는 배치 단위로 sender 스레드에 의해 브로커로 요청을 전송한다. 이 요청에는 여러 메시지가 담겨있으며 브로커는 acks 옵션에 따라 각 메시지에 대한 ACK 응답을 전송해야 한다.
요청에 포함된 모든 메시지가 ACK를 받으면 하나의 요청이 완료되는 것이다. 

이전 요청의 응답이 아직 오지 않았음에도 다음 요청을 보낸다는 것이다. 이를 통해 병렬성을 극대화할 수 있지만, 이전 요청에 대한 재전송이 발생하면 파티션 내 메시지 순서가 꼬일 수 있다.

```text
키가 있는 메시지의 경우 파티션 내에서는 순서가 일반적으로 보장된다.
하지만 병렬 요청이 허용되는 프로듀서 환경에서는 요청 장애로 인한 재전송 메커니즘이 발생하여 순서가 보장되지 않을 수 있다.
만약 메시지 순서가 중요한 토픽이라면 max.in.flight.requests.per.connection를 1로 변경하거나
메시지 자체에 타임스탬프를 추가하고 소비자 측에서 타임스탬프를 활용하여 순서를 보장하는 방법을 사용해야 한다.
max.in.flight.reqeust.per.connection 값을 줄이는 것은 카프카의 핵심 성능에 악영향을 주는 것이므로 가능하면 줄이지 않는 것이 실무에서는 권장된다.
```

```text
타임스탬프를 사용하여 메시지 순서를 보장하는 방식은 타임스탬프 기반 정렬 작업이 추가되어야 한다는 점에서 실시간 처리가 어렵다.
이런 방식은 배치 처리 방식과 어울리며 정렬에 따른 추가 오버헤드도 발생하므로 더욱 실시간 처리에 좋은 방법은 아니다.
```

### 전송 보장 모델(Delivery Guarantee Model) 

메시지가 브로커에 전달되는 방식을 보장하는 모델이며 크게 `at-most-once`, `at-least-once`, `exactly-once`로 나뉜다.

#### At-Most-Once (최대 한 번 전송)
메시지를 최대 한 번만 전송하는 방식으로 별도의 재전송 기능을 활성화하지 않아 속도가 빠르고 메시지 중복 처리 가능성이 없다. 
하지만 메시지가 중간에 손실되더라도 재전송 기능이 없으므로 메시지 손실 가능성이 존재한다. 카프카에서는 프로듀서 옵션 `acks=0` 으로 해당 모델을 제공하고 있다.

- 프로듀서가 브로커에게 메시지를 보낼 때, 브로커로부터 ACK 응답을 받지 않고 계속 보낸다.
- 브로커 측에서 어떠한 장애로 인해 메시지를 정상적으로 기록하지 못하더라도 재전송 받을 방법이 없다.

#### At-Least-Once (적어도 한 번 전송)
메시지를 최소 한 번 전달되며, 메시지 손실을 발생하지 않는 것에 초점을 둔다. 전송에 장애가 발생하면 재전송 메커니즘이 동작하며 데이터 손실이 허용되지 않는 시스템에 적합하다.
단, 재전송 과정에서 같은 데이터가 중복으로 처리될 수 있어 데이터 정확성을 보장하지는 못한다. 해당 모델을 사용하여 중복 제거를 위해서는 소비자 측의 추가 중복 감지 로직이 필요하다.
카프카에서는 `acks-all` 옵션과 `retries` 활성화를 통해 해당 모델을 제공하고 있다.

- 프로듀서가 브로커에게 메시지를 보내고 ACK 응답을 받고 난 이후 다음 메시지를 전송한다.
- 브로커 측에서 메시지를 정상적으로 기록을 했더라도 ACK 응답이 네트워크 예외로 프로듀서에게 전달되지 않았다면 프로듀서는 같은 메시지를 다시 보내게 된다.

#### Exactly-Once (정확히 한 번 전송)
메시지가 정확히 한 번만 전달되도록 보장하는 모델이다. 중복 처리와 손실이 발생하지 않지만 구현이 복잡하고 전송 성능 자체는 떨어진다.
카프카에서는 프로듀서 트랜잭션 API를 사용하여 메시지 전송을 원자적으로 처리해야 하며, `enable.idempotence`와 `transactional.id` 설정을 통해 제공한다.

- 프로듀서는 브로커로부터 ACK 응답을 받은 다음에 다음 메시지를 전송하되, `프로듀서 ID`와 `Sequence` 를 헤더에 담아 전송한다.
- Sequence 는 메시지의 고유 번호로 0부터 순차적으로 증가하게 된다.
- 브로커는 같은 프로듀서가 보낸 이전에 기록된 적이 있는 시퀀스는 메시지로 기록하지 않고 ACK 응답만 보낸다. 
- 브로커는 자신이 가진 Sequence 보다 1만큼 큰 Sequence 를 가진 메시지만 브로커에 저장한다.

카프카에서는 정확히 한 번 전송을 위해 `멱등성(idempotence)`을 활성화, `acks=all`, `retries > 0` 등의 설정을 지정해주어야 한다. 
멱등성이란 동일한 작업을 여러 번 수행하더라도 결과가 변하지 않는 특성을 의미한다. 즉, 프로듀서가 동일한 메시지를 여러 번 보내더라도 브로커 측에서
이전에 받아 저장한 메시지임을 인지하고 중복으로 저장하지 않는 것이다.동일한 메시지를 send 메서드를 통해 연속해서 보내는 것에 대한 것이 아닌 프로듀서, 브로커 간의 `retry` 시 중복 제거를 수행하는 메커니즘이다.

멱등석은 파티션별로 메시지 중복을 제거하지만, 프로듀서가 전달한 배치의 모든 메시지의 `전체 성공 또는 전체 실패(원자성)`를 보장하지는 못한다. 
시간이 지나 재전송을 통해 정상적으로 기록될 가능성은 있긴하지만 소비자 측에서 중간 상태의 잘못된 상태가 노출될 가능성이 있다. 이 때문에 멱등성 하나만으로는 정확히 한 번 전송을 완벽하게 보장하지는 못한다.

프로듀서는 인스턴스 구성 시 `transaction.id`를 입력하여 트랜잭션 기능을 활성화할 수 있다. 이렇게 생성된 프로듀서 인스턴스는 메시지 전송 시 트랜잭션 아이디를 함께 전송하며, 브로커는 해당 트랜잭션 아이디를 기반으로 메시지를 관리한다.
메시지들은 브로커에 임시 저장되며, 트랜잭션이 커밋되기 전까지는 소비자에게 노출되지 않는다. 트랜잭션 내 메시지 중 하나라도 실패하거나 트랜잭션이 롤백되면, 브로커는 해당 트랜잭션 내 모든 메시지를 무효화하고 소비자에게 노출하지 않는다.

### 파티셔너 (Partitioner)
메시지를 특정 파티션에 분배하는 역할을 담당한다. 파티션들에 데이터를 균등 분배하거나 특정 키 기반으로 메시지 정렬을 보장하게끔 해준다.
기본적으로 `DefaultPartitioner`라는 내장 파티셔너를 사용하며, 이는 메시지의 키(Key)를 기반으로 파티션을 결정한다. 키가 없는 메시지의 경우에는 `Round-Robin` 또는 `Sticky Partitioner` 방식으로 분배한다.

프로듀서 인스턴스가 생성될 때, 브로커로부터 해당 토픽의 메타정보를 가져오게 된다. 이 메타정보에는 토픽의 파티션 개수가 포함되어 있어 이 정보를 파티셔너에게 알려 사용하게 된다.
중간에 토픽의 파티션 개수가 변경되면 브로커에서 메타데이터가 업데이트되고, 이를 반영한 파티션 정보를 갱신하게 된다.
이때, 이미 각 파티션에 들어가있는 메시지들은 그대로 들어가게 되고 새로운 메시지에 대해서만 업데이트된 파티션 테이블로 분배한다. 이는 이전에 특정 키가 특정 파티션으로 메시지를 전송하던 
부분에 변화를 주지 않게 하기 위함이다.