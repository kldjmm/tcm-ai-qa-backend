package com.lilin.tcmqa.module.user.vo;

import lombok.Data;

@Data
public class UserLoginResponse {

    private Long userId;

    private String username;

    private String nickname;

    private String role;
}