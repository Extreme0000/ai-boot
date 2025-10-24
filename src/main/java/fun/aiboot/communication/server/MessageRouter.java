package fun.aiboot.communication.server;

public interface MessageRouter {
    void route(String userId, String rawMessage);
}
