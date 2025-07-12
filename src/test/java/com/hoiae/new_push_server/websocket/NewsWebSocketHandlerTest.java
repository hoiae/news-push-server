package com.hoiae.new_push_server.websocket;

import com.hoiae.new_push_server.domain.News;
import com.hoiae.new_push_server.exception.DuplicateConnectionException;
import com.hoiae.new_push_server.exception.MissingTokenException;
import com.hoiae.new_push_server.exception.SerializationFailureException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class NewsWebSocketHandlerTest {

    private NewsWebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        handler = new NewsWebSocketHandler();
        session = Mockito.mock(WebSocketSession.class);
    }

    @Test
    void 연결_성공() throws Exception {
        URI uri = new URI("ws://localhost?token=testToken");
        when(session.getUri()).thenReturn(uri);

        assertDoesNotThrow(() -> handler.afterConnectionEstablished(session));
    }

    @Test
    void 중복_토큰_연결시_예외발생() throws Exception {
        URI uri = new URI("ws://localhost?token=testToken");
        when(session.getUri()).thenReturn(uri);

        handler.afterConnectionEstablished(session);

        assertThrows(DuplicateConnectionException.class, () -> {
            handler.afterConnectionEstablished(session);
        });
    }

    @Test
    void 토큰_누락시_예외발생() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost"));

        assertThrows(MissingTokenException.class, () -> {
            handler.afterConnectionEstablished(session);
        });
    }

    @Test
    void 뉴스_전송시_메시지가_모든_세션에_전송된다() throws Exception {
        URI uri = new URI("ws://localhost?token=testToken");
        when(session.getUri()).thenReturn(uri);
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);

        News news = new News("1", "속보", "중요 뉴스", LocalDateTime.now());
        doNothing().when(session).sendMessage(any(TextMessage.class));

        try {
            handler.broadcast(news);
        } catch (SerializationFailureException e) {
            log.error("JSON 변환 실패. 뉴스 ID: {}, error: {}", news.getId(), e.getMessage(), e);
            throw e;
        }

        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
    }
}
