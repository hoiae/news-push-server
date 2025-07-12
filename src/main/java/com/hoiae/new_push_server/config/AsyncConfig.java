package com.hoiae.new_push_server.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "newsExecutor")
    public Executor newsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);          // 항상 유지할 최소 스레드 수
        executor.setMaxPoolSize(2);           // 최대 스레드 수 (급증 대응)
        executor.setQueueCapacity(100);       // 작업 대기 큐 크기
        executor.setThreadNamePrefix("news-dispatcher-");
        executor.initialize();
        return executor;
    }
}