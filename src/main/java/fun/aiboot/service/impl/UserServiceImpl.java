package fun.aiboot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.aiboot.entity.User;
import fun.aiboot.mapper.UserMapper;
import fun.aiboot.service.UserService;
import fun.aiboot.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @auther putl
 * @since 2025-10-30
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public String login(String username, String password) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");

        User user = this.baseMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
                .eq(User::getPassword, password)
        );

        Assert.notNull(user, "username or password error");

        log.info("用户登录成功：{}", username);
        // 生成token
        return JwtUtil.generateJwt(Map.of(
                "id", user.getId(),
                "username", username,
                "modelId", user.getModelId()
        ));
    }

    @Override
    public void register(String username, String password, String email) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");
        Assert.notNull(email, "email cannot be null");

        this.baseMapper.insert(User.builder()
                .username(username)
                .password(password)
                .email(email)
                .modelId("default")
                .createTime(LocalDateTime.now())
                .build());

        log.info("用户注册成功：{}", username);
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {

    }

    @Override
    public void forgetPassword(String username, String email) {

    }

    @Override
    public User getUserInfo(String id) {
        return this.getById(id);
    }
}
