package fun.aiboot.controller;

import fun.aiboot.dialogue.llm.memory.ElasticsearchChatMemory;
import fun.aiboot.domain.ChatDoc;
import fun.aiboot.service.VectorSearchService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 向量搜索测试接口
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private ElasticsearchChatMemory elasticsearchChatMemory;

    @Autowired
    private VectorSearchService vectorSearchService;

    /**
     * 向量搜索接口
     * GET /api/search/vector?userId=xxx&query=xxx&topK=5
     */
    @GetMapping("/vector")
    public SearchResult vectorSearch(
            @RequestParam String userId,
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK
    ) {
        log.info("Vector search request - userId: {}, query: {}, topK: {}", userId, query, topK);

        List<Message> messages = elasticsearchChatMemory.getMessagesByVectorSearch(userId, query, topK);

        List<MessageInfo> results = messages.stream()
                .map(msg -> new MessageInfo(
                        msg.getMessageType().getValue(),
                        msg.getText()
                ))
                .collect(Collectors.toList());

        return new SearchResult(
                "vector",
                userId,
                query,
                results.size(),
                results
        );
    }

    /**
     * 按时间顺序获取历史消息
     * GET /api/search/history?userId=xxx&limit=10
     */
    @GetMapping("/history")
    public SearchResult getHistory(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.info("History request - userId: {}, limit: {}", userId, limit);

        List<Message> messages = elasticsearchChatMemory.getMessages(userId, limit);

        List<MessageInfo> results = messages.stream()
                .map(msg -> new MessageInfo(
                        msg.getMessageType().getValue(),
                        msg.getText()
                ))
                .collect(Collectors.toList());

        return new SearchResult(
                "history",
                userId,
                null,
                results.size(),
                results
        );
    }

    /**
     * 清除用户的聊天历史
     * DELETE /api/search/clear?userId=xxx
     */
    @DeleteMapping("/clear")
    public String clearHistory(@RequestParam String userId) {
        log.info("Clear history request - userId: {}", userId);
        elasticsearchChatMemory.clearMessages(userId);
        return "Cleared history for user: " + userId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class SearchResult {
        private String searchType;
        private String userId;
        private String query;
        private int count;
        private List<MessageInfo> results;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MessageInfo {
        private String role;
        private String content;
    }
}
