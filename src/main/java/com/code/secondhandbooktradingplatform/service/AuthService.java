package com.code.secondhandbooktradingplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.code.secondhandbooktradingplatform.common.ServiceException;
import com.code.secondhandbooktradingplatform.entity.User;
import com.code.secondhandbooktradingplatform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password, String role) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ServiceException("请输入账号和密码");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null || !user.getPassword().equals(md5(password))) {
            throw new ServiceException("账号或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new ServiceException("该账号已被禁用");
        }
        if (role != null && !role.isBlank() && !role.equals(user.getRole())) {
            throw new ServiceException("ADMIN".equals(role) ? "该账号不是管理员，请选择前台用户登录" : "该账号是管理员，请选择后台管理员登录");
        }
        user.setPassword(null);
        return user;
    }

    public void register(String username, String password, String nickname) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ServiceException("请输入账号和密码");
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count > 0) {
            throw new ServiceException("该账号已被注册");
        }
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(md5(password));
        user.setNickname(nickname == null || nickname.isBlank() ? username : nickname.trim());
        user.setRole("USER");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
    }
}
