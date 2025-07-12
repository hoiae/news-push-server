package com.hoiae.new_push_server.queue;

public interface NewsMessageQueue {
    String receive() throws InterruptedException;
}

