package fun.aiboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@TableName("chat")
public class Chat {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String from;
    private String content;
    private LocalDateTime createTime;
}