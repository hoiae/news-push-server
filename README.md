# 뉴스 알림 서버 (News Push Server)

> 고객이 WebSocket으로 접속 중일 때, 뉴스 ID가 큐에 들어오면 해당 뉴스를 조회하여 실시간으로 전송하는 서버입니다.
---

## 🛠️ 기술 스택

- Java 17
- Spring Boot 3.x
- WebSocket (Spring WebSocket)
- Spring Data JPA (H2 in-memory DB)
- 테스트: JUnit5, Mockito, Awaitility

---

## ✅ 주요 동작 흐름

1. 클라이언트는 WebSocket으로 접속 (`token` 파라미터 포함)
2. 서버는 세션을 토큰 기준으로 `ConcurrentHashMap`에 저장
3. 뉴스 ID가 큐에 들어오면, DB에서 해당 뉴스 조회
4. 연결된 세션 중 같은 토큰을 가진 사용자에게 뉴스 내용을 전송

---

## ✅ 큐 구조 및 SQS 전환 설계

현재는 In-Memory 기반 큐(`InMemoryNewsQueue`)를 사용하지만, `NewsMessageQueue`인터페이스를 활용해서 향후에 AWS SQS로 쉽게 전환 가능하도록 설계했습니다.
AWS SQS로 전환시, `NewsMessageQueue`의 구현체를 현재 `InMemoryNewsQueue`에서 `SqsNewsQueue`로 변경하면 됩니다. 

### 큐 인터페이스

```java
public interface NewsMessageQueue {
    void send(String newsId);
    String receive();
}
```

### 구현체 예시

| 구현체 | 용도                                  |
|--------|-------------------------------------|
| `InMemoryNewsQueue` | LinkedBlockingQueue를 사용한 구현체, 현재 사용 |
| `SqsNewsQueue` | SQS를 사용하기 위한 로직 추가 개발 후 사용 예정       |
