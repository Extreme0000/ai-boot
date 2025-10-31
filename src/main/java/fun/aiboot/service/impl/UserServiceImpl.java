package fun.aiboot.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.aiboot.context.UserContext;
import fun.aiboot.entity.User;
import fun.aiboot.exception.BusinessException;
import fun.aiboot.mapper.UserMapper;
import fun.aiboot.service.UserService;
import fun.aiboot.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
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
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");

        // 根据用户名查询用户
        User user = this.baseMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
        );

        if (user == null) {
            log.warn("登录失败：用户不存在 - {}", username);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("登录失败：密码错误 - {}", username);
            throw new BusinessException(401, "用户名或密码错误");
        }

        log.info("用户登录成功：{}", username);
        // 生成token
        return JwtUtil.generateJwt(UserContext.builder()
                .userId(user.getId())
                .username(username)
                .roles(List.of("ROLE_USER"))
                .roleModelIds(List.of("default"))
                .roleToolIds(List.of("default"))
                .currentModelId("default")
                .enabledToolIds(List.of("default"))
                .lastUpdated(LocalDateTime.now())
                .build());
    }

    @Override
    public void register(String username, String password, String email) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");
        Assert.notNull(email, "email cannot be null");

        // 检查用户名是否已存在
        Long count = this.baseMapper.selectCount(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
        );

        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(password);

        this.baseMapper.insert(User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .modelId("default")
                .createTime(LocalDateTime.now())
                .build());

        log.info("用户注册成功：{}", username);
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(oldPassword, "oldPassword cannot be null");
        Assert.notNull(newPassword, "newPassword cannot be null");

        // 查询用户
        User user = this.baseMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
        );

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 加密新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // 更新密码
        user.setPassword(encodedNewPassword);
        this.baseMapper.updateById(user);

        log.info("用户密码修改成功：{}", username);
    }

    @Override
    public void forgetPassword(String username, String email) {
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(email, "email cannot be null");

        // 查询用户
        User user = this.baseMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
                .eq(User::getEmail, email)
        );

        if (user == null) {
            throw new BusinessException("用户名或邮箱错误");
        }

        // TODO: 这里应该发送重置密码邮件
        // 暂时生成一个随机密码
        String newPassword = "temp123456";
        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        this.baseMapper.updateById(user);

        log.info("用户密码重置成功：{}, 新密码：{}", username, newPassword);
        // 实际应该发送邮件通知用户
    }

    @Override
    public User getUserInfo(String id) {
        return this.getById(id);
    }
}
