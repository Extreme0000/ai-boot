package fun.aiboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "chat_memory") // ES 索引名
public class ChatDoc {
    @Id
    private String id;  // 改为 String 类型，使用 UUID

    @Field(type = FieldType.Keyword)
    private String userId;  // 用户ID

    @Field(type = FieldType.Keyword)
    private String role;  // USER 或 ASSISTANT

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;  // 消息内容

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createTime;

    // 向量字段用于语义搜索（1024，适配 DashScope text-embedding-v2）
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private List<Float> embedding;
}
