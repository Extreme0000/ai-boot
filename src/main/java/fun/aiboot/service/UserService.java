package fun.aiboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.aiboot.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @auther putl
 * @since 2025-10-30
 */
public interface UserService extends IService<User> {

    // 登录
    String login(String username, String password);

    // 注册
    void register(String username, String password, String email);

    // 修改密码
    void updatePassword(String username, String oldPassword, String newPassword);

    // 忘记密码
    void forgetPassword(String username, String email);

    // 获取用户信息
    User getUserInfo(String id);
}
