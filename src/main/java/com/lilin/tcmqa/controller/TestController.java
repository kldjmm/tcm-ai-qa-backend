package com.lilin.tcmqa.controller;

import com.lilin.tcmqa.common.Result;
import com.lilin.tcmqa.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test/success")
    public Result<String> Success() {
        return Result.success("success");
    }
    @GetMapping("/test/error")
    public Result<Void> error() {
       throw new BusinessException(400,"error");
    }
}
