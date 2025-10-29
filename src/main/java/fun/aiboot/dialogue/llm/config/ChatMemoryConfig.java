package fun.aiboot.dialogue.llm.config;

import fun.aiboot.dialogue.llm.memory.ChatMemory;
import fun.aiboot.dialogue.llm.memory.MySQLChatMemory;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Resource
    MySQLChatMemory mySQLChatMemory;

    @Bean
    public ChatMemory chatMemory() {
        return mySQLChatMemory;
    }

}
