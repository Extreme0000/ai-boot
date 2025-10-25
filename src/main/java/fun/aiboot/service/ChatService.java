package fun.aiboot.service;

import fun.aiboot.communication.domain.BaseMessage;
import fun.aiboot.communication.domain.ChatMessage;
import fun.aiboot.communication.server.MessageHandler;
import fun.aiboot.communication.server.MessagePublisher;
import fun.aiboot.dialogue.llm.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ChatService implements MessageHandler {

    @Autowired
    private LLMService llmService;
    @Autowired
    private MessagePublisher messagePublisher;

    @Override
    public String getType() {
        return "chat";
    }

//    @Override
//    public void handleMessage(String userId, BaseMessage message) {
//        ChatMessage msg = (ChatMessage) message;
//        String response = llmService.chat(userId, msg.getContent());
//        ChatMessage chatMessage = new ChatMessage(msg.getFrom(), msg.getTo(), response, LocalDateTime.now(), "text");
//        messagePublisher.sendToUser(userId, chatMessage);
//    }

    @Override
    public void handleMessage(String userId, BaseMessage message) {
        ChatMessage msg = (ChatMessage) message;
        Flux<String> stream = llmService.stream(userId, msg.getContent());

        // 使用 StringBuilder 收集流式响应
        StringBuilder responseBuilder = new StringBuilder();

        // 订阅流并处理每个响应片段
        stream.subscribe(
                chunk -> {
                    responseBuilder.append(chunk);
                    // 发送流式响应片段给用户
                    ChatMessage chatMessage = new ChatMessage(
                            msg.getFrom(),
                            msg.getTo(),
                            chunk,  // 发送当前片段
                            LocalDateTime.now(),
                            "text"
                    );
                    messagePublisher.sendToUser(userId, chatMessage);
                },
                error -> {
                    // 处理错误情况
                    System.err.println("Error in streaming: " + error.getMessage());
                },
                () -> {
                    // 流完成后的处理（可选）
                    log.info("Stream completed 响应：\n{}", responseBuilder);
                }
        );
    }

}
