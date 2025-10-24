package org.example.xiaoaiesp32.dialogue.llm.providers;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.example.xiaoaiesp32.dialogue.llm.tool.ToolsGlobalRegistry;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.tool.ToolCallback;
import reactor.core.publisher.Flux;

import java.util.List;

public class DashscopeModel implements ChatModel {

    private final DashScopeChatModel dashScopeChatModel;


    public DashscopeModel(String modelName, String apiKey) {
        dashScopeChatModel = DashScopeChatModel.builder()
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel(modelName)
                        .build())
                .dashScopeApi(DashScopeApi.builder()
                        .apiKey(apiKey)  // 设置有效的 API 密钥
                        .build())
                .build();
    }

    public DashscopeModel(String modelName, String apiKey, ToolCallingManager toolCallingManager, List<ToolCallback> toolCallbacks) {
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


    @Override
    public String call(String message) {
        return dashScopeChatModel.call(message);
    }

    @Override
    public String call(Message... messages) {
        return dashScopeChatModel.call(messages);
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        return dashScopeChatModel.call(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return dashScopeChatModel.getDefaultOptions();
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
            if (toolCallingManager != null) {
                List<ToolCallback> list = toolsGlobalRegistry.getAllFunctions().entrySet().stream().map(entry -> entry.getValue()).toList();
                return new DashscopeModel(modelName, apiKey, toolCallingManager, list);
            }
            return new DashscopeModel(modelName, apiKey);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
