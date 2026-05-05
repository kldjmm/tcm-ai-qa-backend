package com.lilin.tcmqa.module.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lilin.tcmqa.common.Result;
import com.lilin.tcmqa.module.user.dto.UserLoginRequest;
import com.lilin.tcmqa.module.user.dto.UserRegisterRequest;
import com.lilin.tcmqa.module.user.entity.User;
import com.lilin.tcmqa.module.user.mapper.UserMapper;
import com.lilin.tcmqa.module.user.service.UserService;
import com.lilin.tcmqa.module.user.vo.UserLoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/api/user/list")
    public Result<List<User>> list(){
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getId));
        return Result.success(users);
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid UserRegisterRequest request) {
        Long userId = userService.register(request);
        return Result.success(userId);
    }
    @PostMapping("/login")
    public Result<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return Result.success(response);
 }}