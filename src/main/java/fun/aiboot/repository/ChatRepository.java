package fun.aiboot.repository;

import fun.aiboot.domain.ChatDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends ElasticsearchRepository<ChatDoc, String> {

    /**
     * 根据用户ID查询聊天记录
     */
    List<ChatDoc> findByUserId(String userId);

    /**
     * 根据用户ID和角色查询
     */
    List<ChatDoc> findByUserIdAndRole(String userId, String role);

    /**
     * 根据用户ID分页查询（按时间倒序）
     */
    Page<ChatDoc> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);
}
