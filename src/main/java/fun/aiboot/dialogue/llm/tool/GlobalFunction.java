package fun.aiboot.dialogue.llm.tool;

import org.springframework.ai.tool.ToolCallback;

public interface GlobalFunction {
    ToolCallback getFunctionCallTool();
}