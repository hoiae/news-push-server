package com.hoiae.new_push_server.integration;

import com.hoiae.new_push_server.domain.News;
import com.hoiae.new_push_server.repository.NewsRepository;
import com.hoiae.new_push_server.websocket.NewsWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class NewsIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsWebSocketHandler webSocketHandler;

    @Test
    void DB에서_뉴스를_조회해_전송한다() throws Exception {
        // given: DB에 뉴스 저장
        News news = new News("999", "통합 테스트", "내용입니다", LocalDateTime.now());
        newsRepository.save(news);

        // WebSocket 세션 설정
        WebSocketSession session = mock(WebSocketSession.class);
        URI uri = new URI("ws://localhost/ws/news?token=testToken");
        when(session.getUri()).thenReturn(uri);
        when(session.isOpen()).thenReturn(true);
        doNothing().when(session).sendMessage(any(TextMessage.class));

        webSocketHandler.afterConnectionEstablished(session);

        // when: DB에서 조회해서 전송
        News saved = newsRepository.findById("999").orElseThrow();
        webSocketHandler.broadcast(saved);

        // then: 메시지 전송 확인
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
}
