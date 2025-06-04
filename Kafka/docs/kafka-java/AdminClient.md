# JAVA AdminClient API

AdminClient 클래스는 카프카 시스템과 통신하여 토픽 관리, 구성 관리, 클러스터 정보 조회, 
컨슈머 그룹 관리 등 전반적인 관리 기능을 제공한다.

### AdminClient 인스턴스 생성

```java
void createAdminClient() {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    try (AdminClient adminClient = AdminClient.create(config)) {
        // Kafka 관리 작업 수행
        System.out.println("AdminClient가 성공적으로 생성되었습니다.");
    }
}
```
**AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG** 는 카프카 콘솔 클라이언트에서 브로커 서버를 제공하던 옵션인 '**--bootstrap-server**' 와 같은 요소라고 볼 수 있다.

---
### 토픽 생성 (createTopics)
```java
void createTopics() {
    try (AdminClient adminClient = AdminClient.create(config)) {
        // 순서대로 토픽 이름, 파티션 수, 복제 팩터 수
        NewTopic newTopic = new NewTopic("playground-kafka-topic", 1, (short) 1);
        adminClient.createTopics(Collections.singleton(newTopic))
                .all()
                .get();
    }
}
```
createTopics 메서드는 새로운 토픽을 생성하기 위해 사용되며 **비동기**로 동작하고 컬렉션으로 토픽을 받는다.
비동기로 동작하고 반환을 Future 객체로 하기 때문에 결과 확인을 위해 get 메서드를 통해 스레드를 블로킹하고 있다.

