package fun.aiboot.communication.server;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

public interface SessionManager {
    void register(String userId, WebSocketSession session);
    void remove(String userId);
    WebSocketSession getSession(String userId);
    Collection<WebSocketSession> getAllSessions();
}
