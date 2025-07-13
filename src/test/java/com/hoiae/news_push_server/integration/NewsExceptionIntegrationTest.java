package com.hoiae.news_push_server.integration;


import static java.net.URI.create;
import static org.mockito.Mockito.*;

import com.hoiae.news_push_server.domain.News;
import com.hoiae.news_push_server.queue.InMemoryNewsQueue;
import com.hoiae.news_push_server.repository.NewsRepository;
import com.hoiae.news_push_server.websocket.NewsWebSocketHandler;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.WebSocketSession;

@SpringBootTest
class NewsExceptionIntegrationTest {

    @Autowired
    private InMemoryNewsQueue queue;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsWebSocketHandler handler;

    @Test
    void 존재하지_않는_ID를_큐에_넣으면_예외없이_무시된다() {
        // given: 존재하지 않는 뉴스 ID 전송
        var session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getUri()).thenReturn(create("ws://localhost/ws/news?token=testToken"));

        try {
            handler.afterConnectionEstablished(session);
        } catch (Exception ignored) {}

        // when: DB에 없는 ID 전송
        queue.send("없는ID");

        // then: 예외 발생하지 않고 처리됨 (로그로만 남음)
        // 실제 검증은 로그 확인 또는 try-catch 로 테스트 실패 여부 확인 가능
        // 이 테스트는 단순히 예외 발생 여부가 핵심
    }

    @Test
    void 세션이_닫힌경우_메시지는_전송되지_않는다() {
        // given
        News news = new News("888", "닫힌 세션 테스트", "본문", LocalDateTime.now());
        newsRepository.save(news);

        var session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(false); // 닫힌 상태
        when(session.getUri()).thenReturn(create("ws://localhost/ws/news?token=testToken"));

        try {
            handler.afterConnectionEstablished(session);
        } catch (Exception ignored) {}

        // when
        queue.send("888");

        // then
        // 세션이 닫혀 있어 메시지 전송되지 않아야 함
        // -> sendMessage 호출되지 않아야 함
        try {
            verify(session, never()).sendMessage(any());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}