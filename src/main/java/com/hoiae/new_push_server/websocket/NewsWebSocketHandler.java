package com.hoiae.new_push_server.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hoiae.new_push_server.domain.News;
import com.hoiae.new_push_server.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NewsWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public NewsWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        URI uri = session.getUri();
        String query = (uri != null) ? uri.getQuery() : null;

        if (uri == null || query == null || !query.contains("token=")) {
            log.warn("웹소켓 연결 시 토큰이 누락됨. uri={}", uri);
            throw new MissingTokenException();
        }

        String token = extractToken(uri.getQuery());
        if (clients.containsKey(token)) {
            log.warn("중복 연결 시도됨. 이미 연결된 토큰: {}", token);
            throw new DuplicateConnectionException();
        }

        clients.put(token, session);
        log.info("클라이언트 연결됨: token={}", token);
    }

    public void broadcast(News news) {
        Map<String, Object> newsMap = Map.of(
                "id", news.getId(),
                "title", news.getTitle(),
                "content", news.getContent(),
                "publishedAt", news.getPublishedAt()
        );

        String json;
        try {
            json = objectMapper.writeValueAsString(newsMap);
        } catch (JsonProcessingException e) {
            log.error("뉴스 JSON 변환 실패. 뉴스 ID: {}, error: {}", news.getId(), e.getMessage(), e);
            throw new SerializationFailureException(e);
        }

        for (Map.Entry<String, WebSocketSession> entry : clients.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                    log.info("뉴스 전송 완료. token={}, newsId={}", entry.getKey(), news.getId());
                } catch (IOException e) {
                    log.error("뉴스 메시지 전송 실패. token={}, error={}", entry.getKey(), e.getMessage(), e);
                    throw new MessageSendFailureException(e);
                }
            }
        }
    }

    private String extractToken(String query) {
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        return null;
    }
}
