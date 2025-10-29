package fun.aiboot.dialogue.llm.memory;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.aiboot.domain.Chat;
import fun.aiboot.mapper.ChatMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MySQLChatMemory implements ChatMemory {
    @Autowired
    private ChatMapper chatMapper;


    @Override
    public void addMessage(String userId, Message content) {
        if (content instanceof UserMessage userMessage) {
            chatMapper.insert(Chat.builder()
                    .user("USER")
                    .content(userMessage.getText())
                    .build());
        } else {
            chatMapper.insert(Chat.builder()
                    .user("ASSISTANT")
                    .content(content.getText())
                    .build());
        }
        log.info("addMessage: {}", content.getText());
    }

    @Override
    public List<Message> getMessages(String userId, Integer limit) {
        List<Message> messages = chatMapper.selectList(Wrappers.lambdaQuery(Chat.class)
                        .orderByDesc(Chat::getCreateTime)
                        .last(limit != null, "limit " + limit))
                .stream()
                .map(chat -> {
                    if (chat.getUser().equals("USER")) {
                        return new UserMessage(chat.getContent());
                    } else {
                        return new AssistantMessage(chat.getContent());
                    }
                }).collect(Collectors.toList());
        log.info("getMessages: {}", messages);
        return messages;
    }

    @Override
    public void clearMessages(String userId) {

    }

}
