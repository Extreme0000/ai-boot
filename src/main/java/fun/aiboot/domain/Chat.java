package fun.aiboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@TableName("chat")
@AllArgsConstructor
public class Chat {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String user;
    private String content;
    private LocalDateTime createTime;
}
