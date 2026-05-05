package com.lilin.tcmqa.module.chat.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatAskResponse {

    private String question;

    private String answer;

    private List<ReferenceChunkVO> references;
}