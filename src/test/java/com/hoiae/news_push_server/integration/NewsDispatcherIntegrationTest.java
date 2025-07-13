package com.hoiae.news_push_server.integration;

import com.hoiae.news_push_server.domain.News;
import com.hoiae.news_push_server.queue.InMemoryNewsQueue;
import com.hoiae.news_push_server.repository.NewsRepository;
import com.hoiae.news_push_server.websocket.NewsWebSocketHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import org.springframework.web.socket.WebSocketSession;

import static java.net.URI.create;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.*;

@SpringBootTest
class NewsDispatcherIntegrationTest {

    @Autowired
    private InMemoryNewsQueue queue;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsWebSocketHandler handler;

    @Test
    void 큐에서ID를꺼내DB조회후전송한다() {
        // given
        News news = new News("12345", "테스트 제목", "테스트 본문", LocalDateTime.now());
        newsRepository.save(news);

        // mock세션 삽입
        var session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getUri()).thenReturn(create("ws://localhost/ws/news?token=testToken"));
        try {
            handler.afterConnectionEstablished(session);
        } catch (Exception ignored) {}

        // when
        queue.send("12345");

        // then: 비동기 전송이므로 일정 시간 대기 후 검증
        await().atMost(3, SECONDS).untilAsserted(() ->
                verify(session, times(1)).sendMessage(any()));
    }

    @AfterEach
    void tearDown() {
        newsRepository.deleteAll();
    }
}
