package fun.aiboot.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.aiboot.domain.Chat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {
}
