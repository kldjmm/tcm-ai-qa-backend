package com.lilin.tcmqa.controller;

import com.lilin.tcmqa.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("中医 AI 知识库问答平台启动成功");
    }

}
