package com.lilin.tcmqa.module.user.service;

import com.lilin.tcmqa.module.user.dto.UserLoginRequest;
import com.lilin.tcmqa.module.user.dto.UserRegisterRequest;
import com.lilin.tcmqa.module.user.vo.UserLoginResponse;

public interface UserService {
    Long register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);
}
