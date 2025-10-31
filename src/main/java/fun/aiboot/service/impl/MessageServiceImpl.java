package fun.aiboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.aiboot.entity.Message;
import fun.aiboot.mapper.MessageMapper;
import fun.aiboot.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author putl
 * @since 2025-10-31
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
