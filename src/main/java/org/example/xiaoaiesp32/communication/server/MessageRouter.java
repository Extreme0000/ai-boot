package org.example.xiaoaiesp32.communication.server;

public interface MessageRouter {
    void route(String userId, String rawMessage);
}
