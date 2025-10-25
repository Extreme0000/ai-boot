package fun.aiboot.dialogue.llm.providers;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import fun.aiboot.dialogue.llm.tool.ToolsGlobalRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.List;

public class DashscopeModel implements ChatModel {

    private final DashScopeChatModel dashScopeChatModel;


    public DashscopeModel(String modelName, String apiKey) {
        this(modelName, apiKey, null, null);
    }

    public DashscopeModel(String modelName, String apiKey, ToolCallingManager toolCallingManager, List<ToolCallback> toolCallbacks) {
        Assert.notNull(apiKey, "apiKey must not be null");
        Assert.notNull(modelName, "modelName must not be null");

        if (toolCallingManager == null || toolCallbacks == null || toolCallbacks.isEmpty()) {
            dashScopeChatModel = DashScopeChatModel.builder()
                    .defaultOptions(DashScopeChatOptions.builder()
                            .withModel(modelName)
                            .build())
                    .dashScopeApi(DashScopeApi.builder()
                            .apiKey(apiKey)  // 设置有效的 API 密钥
                            .build())
                    .build();
        } else {
            dashScopeChatModel = DashScopeChatModel.builder()
                    .defaultOptions(DashScopeChatOptions.builder()
                            .withModel(modelName)
                            .withToolCallbacks(toolCallbacks)
                            .build())
                    .dashScopeApi(DashScopeApi.builder()
                            .apiKey(apiKey)  // 设置有效的 API 密钥
                            .build())
                    .toolCallingManager(toolCallingManager)
                    .build();
        }
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        return dashScopeChatModel.call(prompt);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return dashScopeChatModel.stream(prompt);
    }


    public static class Builder {
        private String modelName = "qwen-plus";
        private String apiKey;
        private ToolCallingManager toolCallingManager;
        private ToolsGlobalRegistry toolsGlobalRegistry;

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder toolCallingManager(ToolCallingManager toolCallingManager) {
            this.toolCallingManager = toolCallingManager;
            return this;
        }

        public Builder toolsGlobalRegistry(ToolsGlobalRegistry toolsGlobalRegistry) {
            this.toolsGlobalRegistry = toolsGlobalRegistry;
            return this;
        }

        public DashscopeModel build() {
            List<ToolCallback> toolCallbacks = toolsGlobalRegistry.getAllFunctions().values().stream().toList();
            if (toolCallingManager == null && toolCallbacks.isEmpty()) {
                return new DashscopeModel(modelName, apiKey);
            }
            return new DashscopeModel(modelName, apiKey, toolCallingManager, toolCallbacks);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
