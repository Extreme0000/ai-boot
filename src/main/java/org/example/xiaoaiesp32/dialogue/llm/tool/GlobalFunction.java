package org.example.xiaoaiesp32.dialogue.llm.tool;

import org.springframework.ai.tool.ToolCallback;

public interface GlobalFunction {
    ToolCallback getFunctionCallTool();
}