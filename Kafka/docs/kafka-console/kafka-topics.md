# 카프카 콘솔 클라이언트 [kafka-topics.sh] CLI API

Kafka 시스템을 설치하면 기본적으로 함게 제공되는 클라이언트 스크립들은 
카프카가 설치된 디렉토리의 /bin 패키지 하위에 존재하며 토픽 관리와 관련된
콘솔 클라이언트는 kafka-topics.sh 로 제공된다.

---
### 토픽 조회
```shell
kafka-topics --bootstrap-server localhost:9092 --list
```
지정한 브로커의 현재 토픽 리스트를 확인할 때 사용한다. 

분산된 여러 브로커 중 하나의 브로커에만 연결해도 전체 클러스터의 토픽 목록을 조회할 수 있다.
이는 Kafka 의 메타데이터를 클러스터들이 공유하고 있기 때문에, 하나의 브로커의 토픽 조회가 
클러스터 내 전체 메타데이터를 조회하는 것과 같다.
```shell
kafka-topics.sh --bootstrap-server broker1:9092, broker2:9002 --list
# 여러 브로커의 토픽 확인, 같은 클러스터에 소속되어 있다면 의미 없음
```

토픽에 대한 상세한 조회를 하기 위해서는 --describe 옵션을 사용한다
```shell
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic test_topic
```
```text
Topic: test_topic  PartitionCount: 1  ReplicationFactor: 1  Configs:
    Topic: test_topic  Partition: 0  Leader: 1  Replicas: 1  Isr: 1
```
---
### 토픽 생성 
```shell
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic my-topic --partitions 3 --replication-factor 2
```
--partitions 와 --replication-factor 를 지정하지 않으면 서버 내 디폴트 값으로 설정되며 기본적으로 각각 1이다.


복제 팩터의 개수는 클러스터의 노드 수보다 클 수 없다는 것을 주의해야 한다.
실무에서는 복제 팩터는 최소 2 이상으로 설정하는 것이 좋다. (클러스터 내 노드도 함께)

