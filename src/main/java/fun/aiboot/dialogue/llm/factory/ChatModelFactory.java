package fun.aiboot.dialogue.llm.factory;

import fun.aiboot.dialogue.llm.providers.DashscopeModel;
import fun.aiboot.dialogue.llm.tool.ToolsGlobalRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatModelFactory {

    @Autowired
    private ToolCallingManager toolCallingManager;
    @Autowired
    private ToolsGlobalRegistry toolsGlobalRegistry;

    // 使用工厂模式创建模型实例，预留可扩展
    public ChatModel takeChatModel(ModelFrameworkType modelFrameworkType) {
        return switch (modelFrameworkType) {
            case dashscope -> DashscopeModel.builder()
                    .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                    .modelName("qwen3-max")
                    .toolCallingManager(toolCallingManager)
                    .toolsGlobalRegistry(toolsGlobalRegistry)
                    .build();
            default -> DashscopeModel.builder()
                    .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                    .modelName("qwen-plus")
                    .toolCallingManager(null)
                    .build();
        };
    }

}
