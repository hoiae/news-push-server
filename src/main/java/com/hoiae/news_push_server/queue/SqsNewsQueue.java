package com.hoiae.news_push_server.queue;

/**
 * AWS SQS 연동용 Queue 구현체
 * - 향후 운영 환경에서 사용 예정
 * - 현재는 InMemoryNewsQueue로 대체
 */
public class SqsNewsQueue implements NewsMessageQueue{
    @Override
    public void send(String newsId) {

    }

    @Override
    public String receive() throws InterruptedException {
        return null;
    }
}
