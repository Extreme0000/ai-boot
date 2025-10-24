package org.example.xiaoaiesp32.dialogue.llm.tool.function;

import org.example.xiaoaiesp32.dialogue.llm.tool.GlobalFunction;
import org.example.xiaoaiesp32.dialogue.llm.tool.ToolCallStringResultConverter;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Test1Function implements GlobalFunction {
    @Override
    public ToolCallback getFunctionCallTool() {
        return toolCallback;
    }

    ToolCallback toolCallback = FunctionToolCallback
            .builder("func_playHuiBen", (Map<String, Integer> params, ToolContext toolContext) -> {
                Integer num = params.get("num");
                return "绘本《" + num + "》播放成功！";
            })
            .toolMetadata(ToolMetadata.builder().returnDirect(true).build())
            .description("绘本播放助手，需要用户提供绘本数字编号")
            .inputSchema("""
                        {
                            "type": "object",
                            "properties": {
                                "num": {
                                    "type": "integer",
                                    "description": "要播放的绘本数字编号"
                                }
                            },
                            "required": ["num"]
                        }
                    """)
            .inputType(Map.class)
            .toolCallResultConverter(ToolCallStringResultConverter.INSTANCE)
            .build();
}
