package com.hoiae.new_push_server.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InMemoryNewQueue implements NewsMessageQueue{
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    public String receive() throws InterruptedException {
        return queue.take();
    }
}
