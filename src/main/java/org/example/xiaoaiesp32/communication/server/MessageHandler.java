package org.example.xiaoaiesp32.communication.server;


import org.example.xiaoaiesp32.communication.domain.BaseMessage;

public interface MessageHandler {

    /**
     * 返回此处理器支持的消息类型
     * 例如 "chat", "notify", "order_update"
     */
    String getType();

    /**
     * 处理消息
     */
    void handleMessage(String userId, BaseMessage message);
}
