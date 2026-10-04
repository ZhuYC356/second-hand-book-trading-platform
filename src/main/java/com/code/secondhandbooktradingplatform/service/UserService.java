package com.code.secondhandbooktradingplatform.service;

import com.code.secondhandbooktradingplatform.common.ServiceException;
import com.code.secondhandbooktradingplatform.dto.PasswordDTO;
import com.code.secondhandbooktradingplatform.dto.ProfileDTO;
import com.code.secondhandbooktradingplatform.entity.User;
import com.code.secondhandbooktradingplatform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    public void updateProfile(Long id, ProfileDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname().trim());
        }
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        userMapper.updateById(user);
    }

    public void updatePassword(Long id, PasswordDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (dto.getOldPassword() == null || !user.getPassword().equals(AuthService.md5(dto.getOldPassword()))) {
            throw new ServiceException("原密码错误");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new ServiceException("新密码长度不能少于6位");
        }
        user.setPassword(AuthService.md5(dto.getNewPassword()));
        userMapper.updateById(user);
    }
}
