package fun.aiboot.service.impl;

import fun.aiboot.entity.UserRole;
import fun.aiboot.mapper.UserRoleMapper;
import fun.aiboot.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联服务实现类
 * </p>
 *
 * @author putl
 * @since 2025-10-30
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
