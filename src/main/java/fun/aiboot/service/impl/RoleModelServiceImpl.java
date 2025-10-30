package fun.aiboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.aiboot.entity.RoleModel;
import fun.aiboot.mapper.RoleModelMapper;
import fun.aiboot.service.RoleModelService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色与模型关联表 服务实现类
 * </p>
 *
 * @author putl
 * @since 2025-10-30
 */
@Service
public class RoleModelServiceImpl extends ServiceImpl<RoleModelMapper, RoleModel> implements RoleModelService {

}
