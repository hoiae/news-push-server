package com.hoiae.new_push_server.queue;

import com.hoiae.new_push_server.domain.News;
import com.hoiae.new_push_server.exception.NewsNotFoundException;
import com.hoiae.new_push_server.repository.NewsRepository;
import com.hoiae.new_push_server.websocket.NewsWebSocketHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsDispatcher {

    private final NewsMessageQueue newsMessageQueue;
    private final NewsRepository newsRepository;
    private final NewsWebSocketHandler webSocketHandler;

    @Async("newsExecutor")
//    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void startDispatcher() {
        log.info("Dispatcher started");
        while (true) {
            try {
                String newsId = newsMessageQueue.receive(); // 블로킹
                News news = newsRepository.findById(newsId)
                        .orElseThrow(() -> new NewsNotFoundException());
                webSocketHandler.broadcast(news);
            } catch (Exception e) {
                log.error("뉴스 전송 처리 중 오류 발생", e);
            }
        }
    }
}