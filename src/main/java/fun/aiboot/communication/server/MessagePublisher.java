package fun.aiboot.communication.server;

public interface MessagePublisher {
    void sendToUser(String userId, Object message);
    void broadcast(Object message);
}
