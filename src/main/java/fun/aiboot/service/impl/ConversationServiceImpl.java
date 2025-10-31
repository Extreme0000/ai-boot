package fun.aiboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.aiboot.entity.Conversation;
import fun.aiboot.mapper.ConversationMapper;
import fun.aiboot.service.ConversationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话 服务实现类
 * </p>
 *
 * @author putl
 * @since 2025-10-31
 */
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {

}
