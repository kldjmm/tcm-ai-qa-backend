package com.lilin.tcmqa.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lilin.tcmqa.exception.BusinessException;
import com.lilin.tcmqa.module.user.dto.UserLoginRequest;
import com.lilin.tcmqa.module.user.dto.UserRegisterRequest;
import com.lilin.tcmqa.module.user.entity.User;
import com.lilin.tcmqa.module.user.mapper.UserMapper;
import com.lilin.tcmqa.module.user.service.UserService;
import com.lilin.tcmqa.module.user.vo.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;


    @Override
    public Long register(UserRegisterRequest request) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(400, "用户已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);
        return user.getId();


    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );

        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        if (!request.getPassword().equals(user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRole(user.getRole());
        return response;
    }
}