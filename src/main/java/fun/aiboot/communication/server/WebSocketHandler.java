package fun.aiboot.communication.server;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 从技术与业务来看：
 * WebSocketHandler 相当于一个event listener，主要负责技术层事件，比如：连接建立成功、接收到消息、连接关闭、连接出错。
 * 因此这里bean只负责技术层事件，不处理业务逻辑。
 */

@Slf4j
@Component
public class WebSocketHandler extends AbstractWebSocketHandler {

    @Resource
    private SessionManager sessionManager;

    @Resource
    private MessageRouter messageRouter;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("连接建立成功：{}", session.getId());
        Map<String, String> headers = getHeadersFromSession(session);
        sessionManager.register(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("接收到文本消息：\n{}", message.getPayload());
        Map<String, String> headers = getHeadersFromSession(session);
        messageRouter.route(session.getId(), message.getPayload());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        log.info("接收到二进制消息：{}", message.getPayload());
        Map<String, String> headers = getHeadersFromSession(session);
        messageRouter.route(session.getId(), message.getPayload().toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.warn("连接关闭：{}", status.getReason());
        Map<String, String> headers = getHeadersFromSession(session);
        sessionManager.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("连接出错：{}", exception.getMessage(), exception);
        Map<String, String> headers = getHeadersFromSession(session);
        sessionManager.remove(session.getId());
    }

    private Map<String, String> getHeadersFromSession(WebSocketSession session) {
        // 尝试从请求头获取设备ID
        String[] deviceKeys = {"device-id", "mac_address", "uuid", "Authorization"};

        Map<String, String> headers = new HashMap<>();

        for (String key : deviceKeys) {
            String value = session.getHandshakeHeaders().getFirst(key);
            if (value != null) {
                headers.put(key, value);
            }
        }
        // 尝试从URI参数中获取
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            if (query != null) {
                for (String key : deviceKeys) {
                    String paramPattern = key + "=";
                    int startIdx = query.indexOf(paramPattern);
                    if (startIdx >= 0) {
                        startIdx += paramPattern.length();
                        int endIdx = query.indexOf('&', startIdx);
                        headers.put(key, endIdx >= 0 ? query.substring(startIdx, endIdx) : query.substring(startIdx));
                    }
                }
            }
        }
        return headers;
    }
}
