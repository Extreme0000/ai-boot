package fun.aiboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 模型表
 * </p>
 *
 * @author putl
 * @since 2025-10-30
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("model")
public class Model implements Serializable {

    /**
     * 模型ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 模型名称
     */
    @TableField("name")
    private String name;

    /**
     * 提供商：DashScope、OpenAI等
     */
    @TableField("provider")
    private String provider;

    /**
     * 最大token数
     */
    @TableField("max_tokens")
    private Integer maxTokens;

    /**
     * 模型描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
