# JAVA KafkaConsumer API

## Kafka Consumer 인스턴스 생성

```java
public void createKafkaConsumer() {
    // 1. Kafka 컨슈머 설정
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092"); // Kafka 브로커 주소
    props.put("group.id", "my-consumer-group");       // 컨슈머 그룹 ID
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // 키 역직렬화
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // 값 역직렬화
    props.put("enable.auto.commit", "false");         // 자동 커밋 여부 (false로 설정 시 수동 커밋)

    // 2. KafkaConsumer 객체 생성
    try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

        // 3. 토픽 구독
        consumer.subscribe(Arrays.asList("topic1")); // 하나 이상의 토픽 구독
    }
}

```
| **설정 항목**                 | **설명**                                                                            | **기본값**       |
|---------------------------|-----------------------------------------------------------------------------------|---------------|
| `bootstrap.servers`       | Kafka 클러스터의 브로커 주소 목록 (예: `"localhost:9092"`)                                     | 필수 설정         |
| `group.id`                | 컨슈머가 속한 컨슈머 그룹 ID. 같은 그룹에 속한 컨슈머끼리 파티션을 나눠 처리함                                    | 필수 설정         |
| `key.deserializer`        | 메시지 키를 역직렬화하는 클래스 (예: `StringDeserializer`)                                       | 필수 설정         |
| `value.deserializer`      | 메시지 값을 역직렬화하는 클래스 (예: `StringDeserializer`)                                       | 필수 설정         |
| `enable.auto.commit`      | 오프셋 자동 커밋 여부 (`true` 또는 `false`)                                                  | `true`        |
| `auto.commit.interval.ms` | 자동 커밋 주기 (밀리초 단위). `enable.auto.commit`이 `true`일 때 동작                             | `5000` (5초)   |
| `auto.offset.reset`       | 초기 오프셋 설정. <br> - `"earliest"`: 가장 오래된 메시지부터 읽기 <br> - `"latest"`: 가장 최근 메시지부터 읽기 | `"latest"`    |
| `fetch.min.bytes`         | 컨슈머가 한 번에 가져올 최소 데이터 크기(바이트).                                                     | `1`           |
| `max.poll.records`        | 한 번의 `poll` 호출로 가져올 최대 레코드 수                                                      | `500`         |
| `session.timeout.ms`      | 컨슈머가 브로커와의 연결을 유지할 최대 시간 (밀리초 단위).                                                | `10000` (10초) |

### 컨슈머 주요 기능
컨슈머의 주요 기능에는 subscribe, poll, commit 이 있다. 

### subscribe 
컨슈머가 특정 토픽 또는 토픽 목록을 구독하도록 설정한다.  
```java
consumer.subscribe(Arrays.asList("topic1", "topic2"));
```
- 동일한 컨슈머 그룹에 속한 컨슈머들은 자동으로 파티션을 나눠 처리한다.
- 구독한 토픽이 변경되면 리밸런싱이 발생한다.

### poll
브로커로부터 메시지를 가져온다. 컨슈머가 주기적으로 호출해야 하는 메서드로, 호출 간격이 너무 길 경우 컨슈머가 그룹에서 제외될 수 있다.

```java
ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
for (ConsumerRecord<String, String> record : records) {
    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
}
```
- 동기적으로 실행되며, 호출 시점에 메시지가 없으면 지정한 시간 만큼 대기하다가 그래도 없으면 빈값으로 반환한다.

### commit
컨슈머가 처리한 메시지의 오프셋을 커밋한다. 해당 커밋의 영향을 브로커의 컨슈머 그룹 오프셋을 관리하는 `__consumer_offsets`에 영향을 준다.
동기 커밋과 비동기 커밋으로 나누어져 기능을 제공하고 있다.

```java
try {
    consumer.commitSync();
} catch (CommitFailedException e) {
    e.printStackTrace();
}
// 동기 커밋 : 오프셋 커밋이 완료될 때까지 호출 블록, 예외 처리를 통한 커밋 실패 처리 가능
```

```java
consumer.commitAsync((offsets, exception) -> {
    if (exception != null) {
        System.err.printf("Commit failed for offsets %s%n", offsets);
    }
});
// 비동기 커밋 : 성능은 좋으나, 실패시 복잡성 증가
```

기본적으로 `enable.auto.commit`은 true 기본값으로 자동 커밋이다. 

## Kafka Consumer 개요

`Consumer`는 토픽에 저장된 메시지를 읽는 역할을 하는 클라이언트 애플리케이션이다. 프로듀서가 메시지를 카프카 브로커로 전송하면, 컨슈머는 브로커에서 메시지를 가져와 처리한다.
카프카 토픽으로부터 데이터를 읽으며, 각 파티션의 오프셋(offset)을 기준으로 메시지를 읽으며 자신이 읽는 마지막 메시지의 오프셋을 브로커에 알리고 다음 메시지를 읽는 방식이다.

브로커의 Topic 파티션에서 메시지를 읽는 역할을 수행한다.
모든 컨슈머들은 고유한 컨슈머 그룹 아이디(group.id)를 가지며 컨슈머 그룹에 소속되어 있어야 한다.

### 컨슈머 그룹 (Consumer Group)

여러 컨슈머가 협력하여 하나의 토픽을 처리할 수 있도록 조직화된 개념이다. 컨슈머 그룹은 카프카 `병렬 처리`와 `확장성`에 핵심적인 역할을 한다.

- 컨슈머 그룹은 하나의 논리적 단위이며, 각 컨슈머는 그룹에 속한다.
- 그룹 내 컨슈머는 토픽의 파티션을 나누어 처리한다. 이때 각 파티션은 그룹 내 하나의 컨슈머에만 할당된다.
- 동일한 컨슈머 그룹에 속한 컨슈머들은 협력하여 메시지를 처리한다.
- 서로 다른 컨슈머 그룹은 동일한 토픽을 독립적으로 소비할 수 있다.

컨슈머 그룹 내의 컨슈머 수와 토픽의 파티션 수를 기반으로 파티션을 컨슈머에게 자동으로 할당한다. 만약 컨슈머 수가 파티션 수보다 많으면 일부 컨슈머는 할당받지 못한다.

#### 메시지 소비

- 각 컨슈머는 자신에게 할당된 파티션에게만 메시지를 소비한다.
- 컨슈머가 메시지를 처리한 후, 오프셋을 커밋하면 다음 메시지 소비를 준비한다.

#### 컨슈머 그룹 재조정(`Rebalancing`)

컨슈머 그룹 내에서 컨슈머가 추가되거나 제거되면 파티션 할당을 재조정한다.

- 새로운 컨슈머가 추가되면 기존 컨슈머가 처리하던 파티션 중 일부를 새로운 컨슈머에게 할당한다.
- 기존 컨슈머가 제거되면 해당 컨슈머가 처리하던 파티션을 다른 컨슈머가 승계받는다.

#### 컨슈머 그룹의 오프셋과 파티션 오프셋 비교

서로 다른 컨슈머 그룹은 같은 토픽이라 할 지라도 토픽 내 메시지들을 중복하여 소비할 수 있다. 특정 컨슈머 그룹이 마지막으로 읽은 메시지의 위치를 나타낸다.
이는 컨슈머 그룹 별로 메시지 소비 상태를 추적하고 관리하기 위함으로 카프카 내부 토픽 `__consumer_offsets` 에 저장/관리된다.
컨슈머 그룹 내 컨슈머 중 메시지를 읽고 처리한 후 `커밋(commit)`을 브로커에게 보낸 시점에 컨슈머 오프셋은 갱신된다.

```text
컨슈머 그룹이 처음 시작할 때 메시지를 어디서부터 읽을지를 결정하는 것이 바로 컨슈머 그룹 오프셋 초기화 설정이다.
새로 생성된 컨슈머 그룹이거나, 기존 그룹의 오프셋 정보가 삭제될 때 해당 설정을 참조한다.
auto.offset.reset 설정을 사용하며 earliest, latest, none 이 있다.
```

또 다른 개념으로 `파티션 오프셋`은 토픽의 특정 파티션에 저장된 메시지의 순서를 기록하는 용도이다. 프로듀서로부터 파티션에 데이터가 들어올 때 오프셋이 증가하면서 기록된다. 

### Fetcher
브로커와의 통신을 통해 토픽 파티션에서 메시지를 가져오고, 이를 컨슈머 애플리케이션이 사용할 수 있도록 제공한다.
컨슈머가 poll 메서드가 호출하면, 이미 가져와서 보관중이던 데이터나, 브로커에 메시지를 요청하여 데이터를 가져온다.
내부적으로 배치 처리 로직이 있고, 비동기적으로 동작하여 효율적으로 메시지를 가져온다.

#### 1) 배치 처리
내부적으로 `ConsumerClientNetwork` 클래스를 사용하여 브로커와의 연결을 관리한다. 자신에게 할당된 파티션에서 읽어야할 오프셋을 추적하고 배치 관련 설정에 따라
한 번에 여러 메시지를 가져온다. 배치 관련 설정에는 `fetch.min.bytes`와 `fetch.max.bytes`가 있다.

`ConsumerClientNetwork`는 Fetcher 내부에서 브로커와 TCP 통신을 하는 역할을 수행한다. 브로커로부터 메시지를 가져오거나
컨슈머 활동 중임을 나타내는 `Heartbeat` 신호 전송 등에 사용된다.

#### 2) 역직렬화 
브로커에게서 전달되는 bytes 코드를 `ConsumerRecords` 클래스 인스턴스로 역직렬화하여 제공한다.

#### 3) 데이터 버퍼링
컨슈머의 poll 메서드가 호출되기 이전에 미리 데이터를 가져와 내부 버퍼에 저장해둔다. 이는 실제 poll 메서드가 호출될 때의 대기 시간을 줄이고
메시지를 제공할 수 있게 하기위함이다. 만약 poll 메서드 호출 시점에 메시지가 없다면 브로커에게 요청한다. 실제 `Linked Queue` 같은 자료구조에 담아둔다.

### Heartbeat Thread
Fetcher 와는 별도의 스레드오 컨슈머 내부에 존재하며 자신(컨슈머)의 정상적인 활동을 브로커에 존재하는 `Group Coordinator`에게 보고하는 역할을 한다.
그룹 코디네이터는 특정 시간동안 연결된 컨슈머들에게 `Heart Beat`를 받지 못하면 해당 컨슈머를 컨슈머 그룹에서 제외하여 `리밸런싱`을 수행한다.