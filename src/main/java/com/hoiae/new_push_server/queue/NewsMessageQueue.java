package com.hoiae.new_push_server.queue;

public interface NewsMessageQueue {
    void send(String newsId);
    String receive() throws InterruptedException;
}

