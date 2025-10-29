package fun.aiboot.dialogue.llm.embedding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文本向量化服务
 * 使用 Spring AI 的 EmbeddingModel 生成文本向量
 */
@Slf4j
@Service
public class EmbeddingService {

    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * 将文本转换为向量
     * @param text 输入文本
     * @return 向量（float 数组）
     */
    public List<Float> embed(String text) {
        try {
            EmbeddingRequest request = new EmbeddingRequest(List.of(text), null);
            EmbeddingResponse response = embeddingModel.call(request);

            // 获取第一个结果的向量
            float[] embedding = response.getResults().get(0).getOutput();

            // 转换为 Float List
            List<Float> floatVector = new ArrayList<>(embedding.length);
            for (float value : embedding) {
                floatVector.add(value);
            }

            log.debug("Generated embedding for text: {} (dimension: {})",
                    text.substring(0, Math.min(50, text.length())), floatVector.size());

            return floatVector;
        } catch (Exception e) {
            log.error("Failed to generate embedding for text: {}", text, e);
            throw new RuntimeException("Embedding generation failed", e);
        }
    }

    /**
     * 批量生成向量
     * @param texts 文本列表
     * @return 向量列表
     */
    public List<List<Float>> embedBatch(List<String> texts) {
        try {
            EmbeddingRequest request = new EmbeddingRequest(texts, null);
            EmbeddingResponse response = embeddingModel.call(request);

            return response.getResults().stream()
                    .map(result -> {
                        float[] embedding = result.getOutput();
                        List<Float> floatList = new ArrayList<>(embedding.length);
                        for (float value : embedding) {
                            floatList.add(value);
                        }
                        return floatList;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to generate embeddings for batch", e);
            throw new RuntimeException("Batch embedding generation failed", e);
        }
    }
}
