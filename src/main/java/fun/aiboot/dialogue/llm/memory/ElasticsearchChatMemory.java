package fun.aiboot.dialogue.llm.memory;

import fun.aiboot.dialogue.llm.embedding.EmbeddingService;
import fun.aiboot.domain.ChatDoc;
import fun.aiboot.repository.ChatRepository;
import fun.aiboot.service.VectorSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Elasticsearch 聊天记忆实现
 * 支持向量搜索的语义检索功能
 */
@Slf4j
@Component
public class ElasticsearchChatMemory implements ChatMemory {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private VectorSearchService vectorSearchService;

    /**
     * 是否启用向量搜索模式
     * true: 使用向量搜索检索相关历史
     * false: 按时间顺序检索最近的历史
     */
    private boolean useVectorSearch = true;

    @Override
    public void addMessage(String userId, Message content) {
        try {
            String role = content instanceof UserMessage ? "USER" : "ASSISTANT";
            String text = content.getText();

            // 生成文本向量
            List<Float> embedding = embeddingService.embed(text);

            // 创建文档
            ChatDoc chatDoc = ChatDoc.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .role(role)
                    .content(text)
                    .createTime(LocalDateTime.now())
                    .embedding(embedding)
                    .build();

            // 保存到 ES
            chatRepository.save(chatDoc);

            log.info("Saved {} message to ES for user: {} (vector dim: {})",
                    role, userId, embedding.size());

        } catch (Exception e) {
            log.error("Failed to save message to ES for user: {}", userId, e);
            throw new RuntimeException("Failed to save message", e);
        }
    }

    @Override
    public List<Message> getMessages(String userId, Integer limit) {
        if (useVectorSearch) {
            // 暂时先用传统方式，向量搜索需要查询向量
            return getMessagesByTime(userId, limit);
        } else {
            return getMessagesByTime(userId, limit);
        }
    }

    /**
     * 基于时间顺序获取最近的消息
     */
    private List<Message> getMessagesByTime(String userId, Integer limit) {
        try {
            int size = limit != null ? limit : 10;

            // 按时间倒序查询，然后反转为正序
            List<ChatDoc> docs = chatRepository.findByUserIdOrderByCreateTimeDesc(
                    userId,
                    PageRequest.of(0, size)
            ).getContent();

            // 反转列表，使其按时间正序排列
            List<Message> messages = docs.stream()
                    .sorted((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()))
                    .map(this::toMessage)
                    .collect(Collectors.toList());

            log.info("Retrieved {} messages by time for user: {}", messages.size(), userId);
            return messages;

        } catch (Exception e) {
            log.error("Failed to get messages for user: {}", userId, e);
            return List.of();
        }
    }

    /**
     * 基于向量相似度检索相关消息
     * @param userId 用户ID
     * @param queryText 查询文本
     * @param topK 返回数量
     */
    public List<Message> getMessagesByVectorSearch(String userId, String queryText, int topK) {
        try {
            // 生成查询向量
            List<Float> queryVector = embeddingService.embed(queryText);

            // 向量搜索
            List<ChatDoc> docs = vectorSearchService.searchSimilar(userId, queryVector, topK);

            List<Message> messages = docs.stream()
                    .map(this::toMessage)
                    .collect(Collectors.toList());

            log.info("Retrieved {} messages by vector search for user: {}", messages.size(), userId);
            return messages;

        } catch (Exception e) {
            log.error("Vector search failed for user: {}, falling back to time-based retrieval", userId, e);
            return getMessagesByTime(userId, topK);
        }
    }

    @Override
    public void clearMessages(String userId) {
        try {
            List<ChatDoc> docs = chatRepository.findByUserId(userId);
            chatRepository.deleteAll(docs);
            log.info("Cleared {} messages for user: {}", docs.size(), userId);
        } catch (Exception e) {
            log.error("Failed to clear messages for user: {}", userId, e);
        }
    }

    /**
     * 将 ChatDoc 转换为 Message
     */
    private Message toMessage(ChatDoc doc) {
        if ("USER".equals(doc.getRole())) {
            return new UserMessage(doc.getContent());
        } else {
            return new AssistantMessage(doc.getContent());
        }
    }

    /**
     * 设置是否使用向量搜索
     */
    public void setUseVectorSearch(boolean useVectorSearch) {
        this.useVectorSearch = useVectorSearch;
    }
}
