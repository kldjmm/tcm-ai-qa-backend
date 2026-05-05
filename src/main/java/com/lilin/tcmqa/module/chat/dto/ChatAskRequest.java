package com.lilin.tcmqa.module.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatAskRequest {

    @NotBlank(message = "问题不能为空")
    private String question;

    private Integer topK;
}