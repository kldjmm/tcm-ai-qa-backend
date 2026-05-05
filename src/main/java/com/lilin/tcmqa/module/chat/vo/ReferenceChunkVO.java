package com.lilin.tcmqa.module.chat.vo;

import lombok.Data;

@Data
public class ReferenceChunkVO {

    private Long chunkId;

    private Long itemId;

    private Integer chunkIndex;

    private String chunkText;
}