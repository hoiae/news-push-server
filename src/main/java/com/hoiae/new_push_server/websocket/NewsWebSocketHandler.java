package com.hoiae.new_push_server.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoiae.new_push_server.domain.News;
import com.hoiae.new_push_server.exception.DuplicateConnectionException;
import com.hoiae.new_push_server.exception.MessageSendFailureException;
import com.hoiae.new_push_server.exception.MissingTokenException;
import com.hoiae.new_push_server.exception.SerializationFailureException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class NewsWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getToken(session);

        if (token == null || token.isBlank()) {
            throw new MissingTokenException();
        }

        if (activeSessions.containsKey(token)) {
            throw new DuplicateConnectionException();
        }

        activeSessions.put(token, session);
        log.info("클라이언트 연결됨: token={}", token);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String token = getToken(session);
        activeSessions.remove(token);
        log.info("클라이언트 연결 종료됨: token={}", token);
    }

    public void broadcast(News news) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(Map.of(
                    "id", news.getId(),
                    "title", news.getTitle(),
                    "body", news.getContent(),
                    "publishedAt", news.getPublishedAt()
            ));
        } catch (JsonProcessingException e) {
            throw new SerializationFailureException(e);
        }

        for (WebSocketSession session : activeSessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                } catch (Exception e) {
                    throw new MessageSendFailureException(e);
                }
            }
        }
    }
    private String getToken(WebSocketSession session) {
        try {
            return session.getUri()
                    .getQuery()
                    .replaceAll("token=", "")
                    .split("&")[0];
        } catch (Exception e) {
            return null;
        }
    }
}
