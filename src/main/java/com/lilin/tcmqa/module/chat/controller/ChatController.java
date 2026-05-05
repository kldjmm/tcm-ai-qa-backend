package com.lilin.tcmqa.module.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lilin.tcmqa.common.Result;
import com.lilin.tcmqa.exception.BusinessException;
import com.lilin.tcmqa.module.ai.service.DeepSeekService;
import com.lilin.tcmqa.module.chat.dto.ChatAskRequest;
import com.lilin.tcmqa.module.chat.vo.ChatAskResponse;
import com.lilin.tcmqa.module.chat.vo.ReferenceChunkVO;
import com.lilin.tcmqa.module.knowledge.entity.KnowledgeChunk;
import com.lilin.tcmqa.module.knowledge.mapper.KnowledgeChunkMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final KnowledgeChunkMapper knowledgeChunkMapper;

    private final DeepSeekService deepSeekService;

    @PostMapping("/ask")
    public Result<ChatAskResponse> ask(@RequestBody @Valid ChatAskRequest request) {
        String question = request.getQuestion();

        int topK = request.getTopK() == null ? 5 : request.getTopK();
        topK = Math.max(1, Math.min(topK, 10));

        String keyword = extractKeyword(question);

        List<KnowledgeChunk> chunks = searchChunks(keyword, topK);

        if (chunks.isEmpty() && !keyword.equals(question)) {
            chunks = searchChunks(question, topK);
        }

        ChatAskResponse response = new ChatAskResponse();
        response.setQuestion(question);

        List<ReferenceChunkVO> references = new ArrayList<>();

        if (chunks.isEmpty()) {
            response.setAnswer("知识库中暂未检索到与该问题相关的充分依据。");
            response.setReferences(references);
            return Result.success(response);
        }

        String prompt = buildPrompt(question, chunks);
        String answer = deepSeekService.chat(prompt);

        for (KnowledgeChunk chunk : chunks) {
            ReferenceChunkVO vo = new ReferenceChunkVO();
            vo.setChunkId(chunk.getId());
            vo.setItemId(chunk.getItemId());
            vo.setChunkIndex(chunk.getChunkIndex());
            vo.setChunkText(chunk.getChunkText());
            references.add(vo);
        }

        response.setAnswer(answer);
        response.setReferences(references);

        return Result.success(response);
    }

    private List<KnowledgeChunk> searchChunks(String keyword, int topK) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(400, "关键词不能为空");
        }

        return knowledgeChunkMapper.selectList(
                new LambdaQueryWrapper<KnowledgeChunk>()
                        .eq(KnowledgeChunk::getStatus, 1)
                        .like(KnowledgeChunk::getChunkText, keyword)
                        .orderByDesc(KnowledgeChunk::getCreateTime)
                        .last("LIMIT " + topK)
        );
    }

    private String buildPrompt(String question, List<KnowledgeChunk> chunks) {
        StringBuilder contextBuilder = new StringBuilder();

        for (int i = 0; i < chunks.size(); i++) {
            KnowledgeChunk chunk = chunks.get(i);
            contextBuilder.append("资料")
                    .append(i + 1)
                    .append("：")
                    .append(chunk.getChunkText())
                    .append("\n\n");
        }

        return """
                你是一个中医知识学习助手，请严格基于下面的知识库资料回答用户问题。

                要求：
                1. 只能基于给定资料回答，不要脱离资料自由发挥。
                2. 如果资料不足，请说明“知识库资料不足，无法给出充分回答”。
                3. 回答用于中医知识学习与资料查询。
                4. 不要进行疾病诊断。
                5. 不要给出具体用药剂量或处方建议。
                6. 回答要清晰、简洁、有条理。

                知识库资料：
                %s

                用户问题：
                %s
                """.formatted(contextBuilder.toString(), question);
    }

    private String extractKeyword(String question) {
        if (question == null) {
            return "";
        }

        String keyword = question.trim();

        keyword = keyword.replace("？", "")
                .replace("?", "")
                .replace("。", "")
                .replace(".", "")
                .replace("，", "")
                .replace(",", "")
                .replace("请问", "")
                .replace("什么是", "")
                .replace("是什么", "")
                .replace("有什么", "")
                .replace("有哪些", "")
                .replace("请介绍一下", "")
                .replace("介绍一下", "")
                .replace("功效", "")
                .replace("作用", "")
                .replace("主治", "")
                .replace("组成", "")
                .replace("的", "");

        keyword = keyword.trim();

        return keyword.isEmpty() ? question.trim() : keyword;
    }
}