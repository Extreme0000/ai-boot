package fun.aiboot.dialogue.llm.memory;

import fun.aiboot.domain.Chat;
import fun.aiboot.mapper.ChatMapper;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MySQLChatMemory implements ChatMemory {

    private final Map<String, Message> memory = new HashMap<>();

    @Resource
    private ChatMapper chatMapper;

    @Override
    public void addMessage(String userId, Message content) {
        memory.put(userId, content);
        if (content instanceof UserMessage userMessage) {
            chatMapper.insert(Chat.builder()
                    .fromUser("user")
                    .content(userMessage.getText())
                    .build());
        } else if (content instanceof AssistantMessage assistantMessage) {
            // 总结内容
            String text = assistantMessage.getText();
            chatMapper.insert(Chat.builder()
                    .fromUser("assistant")
                    .content(assistantMessage.getText())
                    .build());
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + content.getClass().getName());
        }
    }

    // todo 查询建议应该对数据库中的内容进行总结优化处理
    @Override
    public List<Message> getMessages(String userId, Integer limit) {
        return new ArrayList<>();
    }

    @Override
    public void clearMessages(String userId) {

    }
}
