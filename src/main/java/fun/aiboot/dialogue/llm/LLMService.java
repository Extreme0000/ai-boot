package fun.aiboot.dialogue.llm;

import fun.aiboot.dialogue.llm.factory.ChatModelFactory;
import fun.aiboot.dialogue.llm.factory.ModelFrameworkType;
import fun.aiboot.dialogue.llm.memory.ChatMemory;
import fun.aiboot.dialogue.llm.memory.SimpleChatMemory;
import fun.aiboot.dialogue.llm.tool.ToolsGlobalRegistry;
import io.micrometer.common.util.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class LLMService {
    private final ChatModelFactory chatModelFactory;

    private final ChatMemory chatMemory;
    private final ChatModel chatModel;
    private final ToolsGlobalRegistry toolsGlobalRegistry;
    @Value("${spring.ai.dashscope.limit:10}")
    Integer limit;

    public LLMService(ToolCallingManager toolCallingManager,
                      ToolsGlobalRegistry toolsGlobalRegistry,
                      @Value("${spring.ai.dashscope.api-key}") String apiKey,
                      @Value("${spring.ai.dashscope.model-name}") String modelName) {
        Assert.notNull(apiKey, "apiKey cannot be null");
        Assert.notNull(modelName, "modelName cannot be null");

        this.chatModelFactory = ChatModelFactory.builder()
                .modelName(modelName)
                .toolCallingManager(toolCallingManager)
                .toolsGlobalRegistry(toolsGlobalRegistry)
                .apiKey(apiKey)
                .build();
        this.toolsGlobalRegistry = toolsGlobalRegistry;
        this.chatMemory = new SimpleChatMemory();
        this.chatModel = chatModelFactory.takeChatModel(ModelFrameworkType.dashscope);
    }

    public String chat(String userId, String message) {
        Prompt prompt = buildPrompt(userId, message);

        ChatResponse call = chatModel.call(prompt);
        String text = call.getResult().getOutput().getText();
        text = StringUtils.isBlank(text) ? "" : text;

        addHistory(userId, message, text);
        return text;
    }

    public Flux<ChatResponse> stream(String userId, String message) {
        Prompt prompt = buildPrompt(userId, message);
        return chatModel.stream(prompt);
    }

    public Map<String, ToolCallback> getAllFunctions() {
        return toolsGlobalRegistry.getAllFunctions();
    }

    private Prompt buildPrompt(String userId, String message) {
        List<Message> messages = chatMemory.getMessages(userId, limit);
        messages.add(new UserMessage(message));
        return new Prompt(messages);
    }

    private void addHistory(String userId, String userMsg, String assistantMsg) {
        UserMessage userMessage = new UserMessage(userMsg);
        AssistantMessage assistantMessage = new AssistantMessage(assistantMsg);
        chatMemory.addMessage(userId, userMessage);
        chatMemory.addMessage(userId, assistantMessage);
    }
}
