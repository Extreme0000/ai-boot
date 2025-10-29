package fun.aiboot.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import fun.aiboot.domain.ChatDoc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 向量搜索服务
 * 使用 Elasticsearch 的 KNN 搜索功能进行语义相似度检索
 */
@Slf4j
@Service
public class VectorSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 基于向量相似度搜索聊天记录
     * @param userId 用户ID
     * @param queryVector 查询向量
     * @param topK 返回最相似的K条记录
     * @return 相似的聊天记录列表
     */
    public List<ChatDoc> searchSimilar(String userId, List<Float> queryVector, int topK) {
        try {
            // 构建 KNN 查询
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("chat_memory")
                    .knn(k -> k
                            .field("embedding")
                            .queryVector(queryVector)  // 直接传递 List<Float>
                            .k(topK)
                            .numCandidates(100)  // 候选数量
                            .filter(f -> f
                                    .term(t -> t
                                            .field("userId")
                                            .value(userId)
                                    )
                            )
                    )
                    .size(topK)
            );

            SearchResponse<ChatDoc> response = elasticsearchClient.search(searchRequest, ChatDoc.class);

            List<ChatDoc> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            log.info("Vector search found {} similar documents for user: {}", results.size(), userId);
            return results;

        } catch (Exception e) {
            log.error("Vector search failed for user: {}", userId, e);
            throw new RuntimeException("Vector search failed", e);
        }
    }

    /**
     * 搜索最相似的对话（只返回用户消息）
     * @param userId 用户ID
     * @param queryVector 查询向量
     * @param topK 返回数量
     * @return 相似的用户消息列表
     */
    public List<ChatDoc> searchSimilarUserMessages(String userId, List<Float> queryVector, int topK) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("chat_memory")
                    .knn(k -> k
                            .field("embedding")
                            .queryVector(queryVector)  // 直接传递 List<Float>
                            .k(topK * 2)  // 多取一些，因为要过滤
                            .numCandidates(200)
                            .filter(f -> f
                                    .bool(b -> b
                                            .must(m -> m.term(t -> t.field("userId").value(userId)))
                                            .must(m -> m.term(t -> t.field("role").value("USER")))
                                    )
                            )
                    )
                    .size(topK)
            );

            SearchResponse<ChatDoc> response = elasticsearchClient.search(searchRequest, ChatDoc.class);

            List<ChatDoc> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

            log.info("Vector search found {} similar user messages for user: {}", results.size(), userId);
            return results;

        } catch (Exception e) {
            log.error("Vector search for user messages failed: {}", userId, e);
            throw new RuntimeException("Vector search failed", e);
        }
    }
}
